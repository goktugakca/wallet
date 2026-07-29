package com.goktug.wallet.event;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record FraudResult(
        UUID fromAccountId,
        UUID toAccountId,
        BigDecimal amount,
        int score,
        boolean suspicious,
        List<String> reasons
) {}