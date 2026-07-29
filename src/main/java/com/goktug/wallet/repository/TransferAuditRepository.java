package com.goktug.wallet.repository;

import com.goktug.wallet.domain.TransferAudit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransferAuditRepository extends JpaRepository<TransferAudit, UUID> {
}
