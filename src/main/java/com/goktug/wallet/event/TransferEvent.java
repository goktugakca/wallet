package com.goktug.wallet.event;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferEvent(UUID fromAccountId, UUID toAccountId, BigDecimal amount) {
}
