package com.goktug.wallet.config;

import com.goktug.wallet.domain.Account;
import com.goktug.wallet.domain.AccountType;
import com.goktug.wallet.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SystemAccountInitializer implements CommandLineRunner {
    private final AccountRepository accountRepository;
    @Override
    public void run(String... args) throws Exception {
        if(accountRepository.findFirstByType(AccountType.SYSTEM).isEmpty()){
            Account account = new Account();
            account.setOwnerName("SYSTEM");
            account.setType(AccountType.SYSTEM);
            accountRepository.save(account);
        }
    }
}
