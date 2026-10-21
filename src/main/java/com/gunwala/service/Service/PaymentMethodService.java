package com.gunwala.service.Service;

import com.gunwala.dto.BankAccountRequest;
import com.gunwala.dto.PaymentMethodResponse;
import com.gunwala.entity.PaymentMethod;
import com.gunwala.entity.PaymentMethodType;
import com.gunwala.exception.BadRequestException;
import com.gunwala.exception.NotFoundException;
import com.gunwala.repository.PaymentMethodRepository;
import com.gunwala.storage.FileDownload;
import com.gunwala.storage.FileStorageService;
import com.gunwala.storage.StoredFile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class PaymentMethodService {

    private static final Pattern UPI_ID = Pattern.compile("^[\\w.\\-]{2,256}@[A-Za-z]{2,64}$");

    private final PaymentMethodRepository repository;
    private final FileStorageService storage;

    // ------------------------------------------------------------ admin: bank account

    @Transactional
    public PaymentMethodResponse createBankAccount(BankAccountRequest request) {
        PaymentMethod method = new PaymentMethod();
        method.setType(PaymentMethodType.BANK_ACCOUNT);
        applyBankDetails(method, request);
        return PaymentMethodResponse.from(repository.save(method));
    }

    @Transactional
    public PaymentMethodResponse updateBankAccount(Long id, BankAccountRequest request) {
        PaymentMethod method = getOrThrow(id);
        requireType(method, PaymentMethodType.BANK_ACCOUNT);
        applyBankDetails(method, request);
        return PaymentMethodResponse.from(method);
    }

    // ------------------------------------------------------------ admin: QR code

    @Transactional
    public PaymentMethodResponse createQrCode(String displayName, String upiId, MultipartFile qrImage) {
        if (!StringUtils.hasText(displayName)) {
            throw new BadRequestException("displayName is required");
        }
        PaymentMethod method = new PaymentMethod();
        method.setType(PaymentMethodType.QR_CODE);
        method.setDisplayName(displayName.trim());
        method.setUpiId(validateUpiId(upiId));
        applyQrImage(method, storage.storeQrImage(qrImage));
        return PaymentMethodResponse.from(repository.save(method));
    }

    /** Every argument is optional; only provided values are changed. */
    @Transactional
    public PaymentMethodResponse updateQrCode(Long id, String displayName, String upiId, MultipartFile qrImage) {
        PaymentMethod method = getOrThrow(id);
        requireType(method, PaymentMethodType.QR_CODE);
        if (StringUtils.hasText(displayName)) {
            method.setDisplayName(displayName.trim());
        }
        if (upiId != null) {
            method.setUpiId(validateUpiId(upiId));
        }
        if (qrImage != null && !qrImage.isEmpty()) {
            // Old image is intentionally kept on disk: it is evidence of what past payers scanned.
            applyQrImage(method, storage.storeQrImage(qrImage));
        }
        return PaymentMethodResponse.from(method);
    }

    // ------------------------------------------------------------ admin: common

    @Transactional
    public PaymentMethodResponse setActive(Long id, boolean active) {
        PaymentMethod method = getOrThrow(id);
        method.setActive(active);
        return PaymentMethodResponse.from(method);
    }

    @Transactional(readOnly = true)
    public List<PaymentMethodResponse> listAll() {
        return repository.findAll().stream().map(PaymentMethodResponse::from).toList();
    }

    // ------------------------------------------------------------ end user

    @Transactional(readOnly = true)
    public List<PaymentMethodResponse> listActive() {
        return repository.findByActiveTrueOrderByIdAsc().stream().map(PaymentMethodResponse::from).toList();
    }

    /** End users can only see QR images of active methods; admins can see all. */
    @Transactional(readOnly = true)
    public FileDownload loadQrImage(Long id, boolean includeInactive) {
        PaymentMethod method = includeInactive
                ? getOrThrow(id)
                : repository.findByIdAndActiveTrue(id).orElseThrow(() -> notFound(id));
        requireType(method, PaymentMethodType.QR_CODE);
        String ext = "image/png".equals(method.getQrImageContentType()) ? ".png" : ".jpg";
        return new FileDownload(storage.load(method.getQrImagePath()), method.getQrImageContentType(),
                "payment-qr-" + id + ext);
    }

    // ------------------------------------------------------------ helpers

    private void applyBankDetails(PaymentMethod m, BankAccountRequest r) {
        m.setDisplayName(r.displayName().trim());
        m.setAccountHolderName(r.accountHolderName().trim());
        m.setBankName(r.bankName().trim());
        m.setAccountNumber(r.accountNumber().trim());
        m.setIfscCode(r.ifscCode().trim().toUpperCase());
        m.setBranchName(r.branchName() == null ? null : r.branchName().trim());
    }

    private void applyQrImage(PaymentMethod m, StoredFile file) {
        m.setQrImagePath(file.path());
        m.setQrImageContentType(file.contentType());
    }

    private String validateUpiId(String upiId) {
        if (!StringUtils.hasText(upiId)) {
            return null;
        }
        String trimmed = upiId.trim();
        if (!UPI_ID.matcher(trimmed).matches()) {
            throw new BadRequestException("upiId is not a valid UPI ID (e.g. company@okhdfcbank)");
        }
        return trimmed;
    }

    private void requireType(PaymentMethod m, PaymentMethodType expected) {
        if (m.getType() != expected) {
            throw new BadRequestException("Payment method " + m.getId() + " is not of type " + expected);
        }
    }

    private PaymentMethod getOrThrow(Long id) {
        return repository.findById(id).orElseThrow(() -> notFound(id));
    }

    private NotFoundException notFound(Long id) {
        return new NotFoundException("Payment method " + id + " not found");
    }
}
