package com.gunwala.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Fires only after the DB transaction commits, so users are never notified about a change that rolled back.
 * Plug in email / SMS / push notifications here.
 */
@Slf4j
@Component
public class PaymentNotificationListener {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPaymentStatusChanged(PaymentStatusChangedEvent event) {
        switch (event.status()) {
            case PENDING -> log.info("Payment {} submitted by {} — notify admins", event.paymentId(), event.userId());
            case APPROVED -> log.info("Payment {} approved — notify user {}", event.paymentId(), event.userId());
            case REJECTED -> log.info("Payment {} rejected ({}) — notify user {}",
                    event.paymentId(), event.rejectionReason(), event.userId());
        }
    }
}
