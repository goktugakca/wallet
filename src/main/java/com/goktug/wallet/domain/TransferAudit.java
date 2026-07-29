package com.goktug.wallet.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "transfer_audits")
@Getter
@Setter
public class TransferAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID fromAccountId;
    private UUID toAccountId;

    @Column(precision = 19, scale = 4)
    private BigDecimal amount;

    @CreationTimestamp
    private Instant recordedAt;
}