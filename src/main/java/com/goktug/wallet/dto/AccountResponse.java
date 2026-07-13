package com.goktug.wallet.dto;

import com.goktug.wallet.domain.Account;
import com.goktug.wallet.domain.AccountType;

import java.util.UUID;

public record AccountResponse(UUID id, String ownerName, AccountType type) {
    public static AccountResponse from(Account account){
        return new AccountResponse(account.getId(),account.getOwnerName(),account.getType());
    }
}
