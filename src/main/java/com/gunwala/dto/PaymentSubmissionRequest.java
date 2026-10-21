package com.gunwala.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Bound from multipart form fields (@ModelAttribute); the receipt file is a separate part. */
@Getter
@Setter
public class PaymentSubmissionRequest {

    @NotNull
    private Long paymentMethodId;

    @NotNull
    @DecimalMin(value = "1.00", message = "must be at least 1.00")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal amount;

    @NotBlank
    @Size(max = 50)
    @Pattern(regexp = "^[A-Za-z0-9-]+$", message = "may contain only letters, digits and hyphens")
    private String transactionReference;

    @NotNull
    @PastOrPresent
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate paymentDate;

    @Size(max = 500)
    private String remarks;
}
