package com.gunwala.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record BankAccountRequest(
        @NotBlank @Size(max = 100) String displayName,
        @NotBlank @Size(max = 100) String accountHolderName,
        @NotBlank @Size(max = 100) String bankName,
        @NotBlank @Pattern(regexp = "^\\d{9,18}$", message = "must be 9-18 digits") String accountNumber,
        @NotBlank @Pattern(regexp = "^[A-Z]{4}0[A-Z0-9]{6}$", message = "must be a valid IFSC code, e.g. HDFC0001234") String ifscCode,
        @Size(max = 100) String branchName
) {}
