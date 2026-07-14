package com.goktug.wallet.repository;

import com.goktug.wallet.domain.Account;
import com.goktug.wallet.domain.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    Optional<Account> findFirstByType(AccountType type);
}
