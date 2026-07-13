package com.goktug.wallet.dto;

import com.goktug.wallet.domain.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateAccountRequest(@NotBlank String ownerName, @NotNull AccountType type) {
}
