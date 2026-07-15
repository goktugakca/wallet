package com.goktug.wallet.service;

import com.goktug.wallet.domain.Account;
import com.goktug.wallet.domain.AccountType;
import com.goktug.wallet.domain.LedgerEntry;
import com.goktug.wallet.domain.Transaction;
import com.goktug.wallet.dto.DepositRequest;
import com.goktug.wallet.repository.AccountRepository;
import com.goktug.wallet.repository.LedgerEntryRepository;
import com.goktug.wallet.repository.TransactionRepository;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final LedgerEntryRepository ledgerEntryRepository;
    private final TransactionRepository transactionRepository;

    public Account createAccount(String ownerName, AccountType type){
        Account acc = new Account();
        acc.setOwnerName(ownerName);
        acc.setType(type);
        return accountRepository.save(acc);
    }
    public BigDecimal getBalance(UUID accountId){
        return ledgerEntryRepository.sumAmountByAccountId(accountId);
    }
    @Transactional
    public void deposit(UUID accountId,BigDecimal amount){
        Account userAccount = accountRepository.findById(accountId).orElseThrow(() -> new IllegalArgumentException("Account Not Found"));
        Account systemAccount = accountRepository.findFirstByType(AccountType.SYSTEM).orElseThrow(()-> new IllegalStateException("System Account Not Found"));

        Transaction transaction = new Transaction();
        transaction.setDescription("Deposit");
        transaction=transactionRepository.save(transaction);

        LedgerEntry systemLedgerEntry = new LedgerEntry();
        LedgerEntry userLedgerEntry = new LedgerEntry();

        systemLedgerEntry.setAccount(systemAccount);
        systemLedgerEntry.setTransaction(transaction);
        systemLedgerEntry.setAmount(amount.negate());

        userLedgerEntry.setAccount(userAccount);
        userLedgerEntry.setTransaction(transaction);
        userLedgerEntry.setAmount(amount);

        ledgerEntryRepository.save(systemLedgerEntry);
        ledgerEntryRepository.save(userLedgerEntry);
    }

    @Transactional
    public void transfer(UUID fromAccountId,UUID toAccountId,BigDecimal amount){
        Account fromAcc = accountRepository.findByIdForUpdate(fromAccountId).orElseThrow(()-> new IllegalArgumentException("From Account Not Found"));
        Account toAcc = accountRepository.findById(toAccountId).orElseThrow(()-> new IllegalArgumentException("To Account Not Found"));

        if (fromAccountId.equals(toAccountId))
            throw new IllegalArgumentException("Cannot transfer to the same account");

        BigDecimal bakiye = getBalance(fromAccountId);
        if(bakiye.compareTo(amount)<0){
            throw new IllegalStateException("Insufficient Balance");
        }
        Transaction transaction = new Transaction();
        transaction.setDescription("Transfer");
        transactionRepository.save(transaction);

        LedgerEntry fromLedgerEntry = new LedgerEntry();
        LedgerEntry toLedgerEntry = new LedgerEntry();

        fromLedgerEntry.setAccount(fromAcc);
        fromLedgerEntry.setTransaction(transaction);
        fromLedgerEntry.setAmount(amount.negate());

        toLedgerEntry.setAccount(toAcc);
        toLedgerEntry.setTransaction(transaction);
        toLedgerEntry.setAmount(amount);

        ledgerEntryRepository.save(fromLedgerEntry);
        ledgerEntryRepository.save(toLedgerEntry);
    }
}
