package com.gunwala.event;

import com.gunwala.entity.PaymentStatus;
import com.gunwala.service.Service.ShiprocketOrderPlacementService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * When an admin approves a payment, place the Shiprocket order automatically.
 * AFTER_COMMIT: only once the approval (and "order paid") is saved.
 * Async: the admin's Approve click returns immediately; the order page shows progress.
 */
@Component
@RequiredArgsConstructor
public class ShiprocketAutoShipListener {

    private final ShiprocketOrderPlacementService placementService;

    @Async("shippingExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPaymentStatusChanged(PaymentStatusChangedEvent event) {
        if (event.status() == PaymentStatus.APPROVED) {
            placementService.onPaymentApproved(event.paymentId());
        }
    }
}
