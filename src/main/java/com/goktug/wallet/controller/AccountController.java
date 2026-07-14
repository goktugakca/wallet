package com.goktug.wallet.controller;

import com.goktug.wallet.dto.AccountResponse;
import com.goktug.wallet.dto.CreateAccountRequest;
import com.goktug.wallet.dto.DepositRequest;
import com.goktug.wallet.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @PostMapping
    public AccountResponse createAccount(@Valid @RequestBody CreateAccountRequest request){
        return AccountResponse.from(accountService.createAccount(request.ownerName(),request.type()));
    }

    @PostMapping("/{accountId}/deposit")
    public void postDeposit(@PathVariable UUID accountId, @Valid @RequestBody DepositRequest request){
        accountService.deposit(accountId,request.amount());
    }
    @GetMapping("/{accountId}/balance")
    public BigDecimal getBalance(@PathVariable UUID accountId){
        return accountService.getBalance(accountId);
    }
}
