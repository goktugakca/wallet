package com.goktug.wallet.service;

import com.goktug.wallet.domain.Account;
import com.goktug.wallet.domain.AccountType;
import com.goktug.wallet.repository.AccountRepository;
import com.goktug.wallet.repository.LedgerEntryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Testcontainers
public class TransferConcurrencyTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
    @Autowired AccountService accountService;
    @Autowired AccountRepository accountRepository;
    @Autowired LedgerEntryRepository ledgerEntryRepository;
    @Test
    void shouldNotAllowOverdraftUnderConcurrentTransfers() throws InterruptedException {
        Account fromAcc = accountService.createAccount("Gonderen", AccountType.USER);
        Account toAcc = accountService.createAccount("Alici", AccountType.USER);
        accountService.deposit(fromAcc.getId(),new BigDecimal("30"));
        int threadCount = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger failedCount = new AtomicInteger(0);
        for (int i = 0; i < threadCount; i++){
            executor.submit(() -> {
                try {
                    latch.await();
                    accountService.transfer(fromAcc.getId(), toAcc.getId(), new BigDecimal("10"),null);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    failedCount.incrementAndGet();
                }
            });
        }
        latch.countDown();
        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);
        BigDecimal finalBalance = accountService.getBalance(fromAcc.getId());
        System.out.println(">>> FINAL BALANCE: " + finalBalance + " | FAILED: " + failedCount.get());
        assertThat(finalBalance).isGreaterThanOrEqualTo(BigDecimal.ZERO);
    }
}
