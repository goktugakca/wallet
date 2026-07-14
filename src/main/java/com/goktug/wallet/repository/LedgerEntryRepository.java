package com.goktug.wallet.repository;

import com.goktug.wallet.domain.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.UUID;
@Repository
public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, UUID> {
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM LedgerEntry e WHERE e.account.id = :accountId")
    BigDecimal sumAmountByAccountId(@Param("accountId") UUID accountId);
}
