package com.goktug.wallet.controller;

import com.goktug.wallet.dto.AccountResponse;
import com.goktug.wallet.dto.CreateAccountRequest;
import com.goktug.wallet.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @PostMapping
    public AccountResponse createAccount(@Valid @RequestBody CreateAccountRequest request){
        return AccountResponse.from(accountService.createAccount(request.ownerName(),request.type()));
    }
}
