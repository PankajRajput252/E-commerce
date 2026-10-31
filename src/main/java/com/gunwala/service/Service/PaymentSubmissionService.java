package com.gunwala.service.Service;

import com.gunwala.dto.PageResponse;
import com.gunwala.dto.PaymentSubmissionRequest;
import com.gunwala.dto.PaymentSubmissionResponse;
import com.gunwala.entity.PaymentMethod;
import com.gunwala.entity.PaymentStatus;
import com.gunwala.entity.PaymentSubmission;
import com.gunwala.event.PaymentStatusChangedEvent;
import com.gunwala.exception.BadRequestException;
import com.gunwala.exception.ConflictException;
import com.gunwala.exception.NotFoundException;
import com.gunwala.repository.PaymentMethodRepository;
import com.gunwala.repository.PaymentSubmissionRepository;
import com.gunwala.storage.FileDownload;
import com.gunwala.storage.FileStorageService;
import com.gunwala.storage.StoredFile;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import com.gunwala.exception.DuplicateTransactionReferenceException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentSubmissionService {

    private final PaymentSubmissionRepository submissionRepository;
    private final PaymentMethodRepository methodRepository;
    private final FileStorageService storage;
    private final ApplicationEventPublisher events;
    private final ShopOrderService shopOrderService;

    // ============================================================ END USER

    @Transactional
    public PaymentSubmissionResponse submit(String userId, PaymentSubmissionRequest request, MultipartFile receipt) {
        PaymentMethod method = methodRepository.findByIdAndActiveTrue(request.getPaymentMethodId())
                .orElseThrow(() -> new BadRequestException("Selected payment method is not available"));

        String reference = request.getTransactionReference().trim().toUpperCase();
        ensureReferenceNotUsed(reference, userId);

        String orderReference = StringUtils.hasText(request.getOrderReference()) ? request.getOrderReference().trim() : null;
        if (orderReference != null) {
            shopOrderService.validatePayable(orderReference, userId, request.getAmount());
            if (submissionRepository.existsByOrderReferenceAndStatus(orderReference, PaymentStatus.PENDING)) {
                throw new ConflictException("A payment for order " + orderReference + " is already waiting for verification");
            }
        }

        StoredFile file = storage.storeReceipt(receipt);
        deleteFileIfTransactionRollsBack(file.path());

        PaymentSubmission s = new PaymentSubmission();
        s.setUserId(userId);
        s.setPaymentMethod(method);
        s.setAmount(request.getAmount());
        s.setTransactionReference(reference);
        s.setActiveReference(reference);
        s.setPaymentDate(request.getPaymentDate());
        s.setUserRemarks(StringUtils.hasText(request.getRemarks()) ? request.getRemarks().trim() : null);
        s.setOrderReference(orderReference);
        s.setReceiptPath(file.path());
        s.setReceiptContentType(file.contentType());
        s.setReceiptOriginalName(file.originalName());
        s.setStatus(PaymentStatus.PENDING);
        try {
            submissionRepository.saveAndFlush(s);
        } catch (DataIntegrityViolationException ex) {
            // Two requests with the same reference got past the check at once (e.g. double-tap on Submit)
            if (isActiveReferenceViolation(ex)) {
                throw new DuplicateTransactionReferenceException(
                        "A payment with transaction reference " + reference + " has already been submitted. "
                                + "Please check your payment history before submitting again.",
                        reference, null);
            }
            throw ex;
        }

        events.publishEvent(new PaymentStatusChangedEvent(s.getId(), userId, PaymentStatus.PENDING, null));
        return PaymentSubmissionResponse.from(s, false);
    }

    /**
     * Rejects a reference already attached to a PENDING or APPROVED payment.
     * Tailored message for the user's own payment; nothing about someone else's payment is revealed.
     */
    private void ensureReferenceNotUsed(String reference, String userId) {
        submissionRepository.findFirstByTransactionReferenceAndStatusNotOrderByCreatedAtDesc(reference, PaymentStatus.REJECTED)
                .ifPresent(existing -> {
                    if (userId.equals(existing.getUserId())) {
                        String state = existing.getStatus() == PaymentStatus.APPROVED
                                ? "it has already been approved"
                                : "it is currently under review";
                        throw new DuplicateTransactionReferenceException(
                                "You have already submitted a payment with transaction reference " + reference
                                        + " and " + state + ". Please do not submit the same payment twice.",
                                reference, existing.getId());
                    }
                    throw new DuplicateTransactionReferenceException(
                            "Transaction reference " + reference + " has already been used for another payment. "
                                    + "Please check the reference number from your bank or UPI app and try again.",
                            reference, null);
                });
    }

    private boolean isActiveReferenceViolation(DataIntegrityViolationException ex) {
        String msg = ex.getMostSpecificCause().getMessage();
        return msg != null && msg.contains(PaymentSubmission.UK_ACTIVE_REFERENCE);
    }

    @Transactional(readOnly = true)
    public List<PaymentSubmissionResponse> listForUser(String userId) {
        return submissionRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(s -> PaymentSubmissionResponse.from(s, false))
                .toList();
    }

    @Transactional(readOnly = true)
    public PaymentSubmissionResponse getForUser(Long id, String userId) {
        return PaymentSubmissionResponse.from(getOwnedOrThrow(id, userId), false);
    }

    @Transactional(readOnly = true)
    public FileDownload loadReceiptForUser(Long id, String userId) {
        return toDownload(getOwnedOrThrow(id, userId));
    }

    // ============================================================ ADMIN

    @Transactional(readOnly = true)
    public PageResponse<PaymentSubmissionResponse> listForAdmin(PaymentStatus status, Pageable pageable) {
        Page<PaymentSubmission> page = status == null
                ? submissionRepository.findAll(pageable)
                : submissionRepository.findByStatus(status, pageable);
        return PageResponse.of(page.map(s -> PaymentSubmissionResponse.from(s, true)));
    }

    @Transactional(readOnly = true)
    public PaymentSubmissionResponse getForAdmin(Long id) {
        return PaymentSubmissionResponse.from(getOrThrow(id), true);
    }

    @Transactional(readOnly = true)
    public FileDownload loadReceiptForAdmin(Long id) {
        return toDownload(getOrThrow(id));
    }

    @Transactional
    public PaymentSubmissionResponse approve(Long id, String adminUsername) {
        PaymentSubmission s = getOrThrow(id);
        ensurePending(s);
        s.setStatus(PaymentStatus.APPROVED);
        s.setRejectionReason(null);
        s.setReviewedBy(adminUsername);
        s.setReviewedAt(Instant.now());

        // Same transaction: the order becomes PAID together with the approval.
        // After commit, ShiprocketAutoShipListener places the Shiprocket order in the background.
        if (StringUtils.hasText(s.getOrderReference())) {
            shopOrderService.markPaid(s.getOrderReference(), s.getId());
        }

        events.publishEvent(new PaymentStatusChangedEvent(s.getId(), s.getUserId(), PaymentStatus.APPROVED, null));
        return PaymentSubmissionResponse.from(s, true);
    }

    @Transactional
    public PaymentSubmissionResponse reject(Long id, String adminUsername, String reason) {
        if (!StringUtils.hasText(reason)) {
            throw new BadRequestException("Rejection reason is required");
        }
        PaymentSubmission s = getOrThrow(id);
        ensurePending(s);
        s.setStatus(PaymentStatus.REJECTED);
        s.setActiveReference(null);   // frees the reference so the user can submit it again
        s.setRejectionReason(reason.trim());
        s.setReviewedBy(adminUsername);
        s.setReviewedAt(Instant.now());

        events.publishEvent(new PaymentStatusChangedEvent(s.getId(), s.getUserId(), PaymentStatus.REJECTED, s.getRejectionReason()));
        return PaymentSubmissionResponse.from(s, true);
    }

    // ============================================================ helpers

    private void ensurePending(PaymentSubmission s) {
        if (s.getStatus() != PaymentStatus.PENDING) {
            throw new ConflictException("Payment " + s.getId() + " is already " + s.getStatus() + " and cannot be changed");
        }
    }

    private PaymentSubmission getOrThrow(Long id) {
        return submissionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Payment " + id + " not found"));
    }

    /** Returns 404 (not 403) for other users' payments so IDs can't be probed. */
    private PaymentSubmission getOwnedOrThrow(Long id, String userId) {
        return submissionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NotFoundException("Payment " + id + " not found"));
    }

    private FileDownload toDownload(PaymentSubmission s) {
        return new FileDownload(storage.load(s.getReceiptPath()), s.getReceiptContentType(), s.getReceiptOriginalName());
    }

    /** If the DB insert fails, don't leave an orphaned receipt file on disk. */
    private void deleteFileIfTransactionRollsBack(String path) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCompletion(int status) {
                    if (status == STATUS_ROLLED_BACK) {
                        storage.deleteQuietly(path);
                    }
                }
            });
        }
    }
}
