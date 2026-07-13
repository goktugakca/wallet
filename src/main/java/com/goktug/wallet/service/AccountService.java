package com.goktug.wallet.service;

import com.goktug.wallet.domain.Account;
import com.goktug.wallet.domain.AccountType;
import com.goktug.wallet.repository.AccountRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;

    public Account createAccount(String ownerName, AccountType type){
        Account acc = new Account();
        acc.setOwnerName(ownerName);
        acc.setType(type);
        return accountRepository.save(acc);
    }
}
