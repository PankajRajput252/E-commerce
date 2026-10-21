package com.gunwala.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

/**
 * A payment option configured by the admin. Either a bank account or a QR code.
 * Never hard-deleted: deactivate instead, because past submissions reference it.
 */
@Entity
@Table(name = "payment_methods")
@Getter
@Setter
@NoArgsConstructor
public class PaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentMethodType type;

    /** Label shown to the user, e.g. "HDFC Current Account" or "Company UPI QR". */
    @Column(nullable = false, length = 100)
    private String displayName;

    // ---- Bank account fields (type = BANK_ACCOUNT) ----
    @Column(length = 100)
    private String accountHolderName;

    @Column(length = 100)
    private String bankName;

    @Column(length = 30)
    private String accountNumber;

    @Column(length = 11)
    private String ifscCode;

    @Column(length = 100)
    private String branchName;

    // ---- QR code fields (type = QR_CODE) ----
    @Column(length = 100)
    private String upiId;

    @Column(length = 255)
    private String qrImagePath;

    @Column(length = 50)
    private String qrImageContentType;

    @Column(nullable = false)
    private boolean active = true;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}
