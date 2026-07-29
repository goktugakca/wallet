package com.goktug.wallet.repository;

import com.goktug.wallet.domain.FlaggedTransfer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FlaggedTransferRepository extends JpaRepository<FlaggedTransfer, UUID> {
}
