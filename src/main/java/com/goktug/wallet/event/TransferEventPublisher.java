package com.goktug.wallet.event;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransferEventPublisher {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void publishTransferEvent(UUID fromAccountId, UUID toAccountId, BigDecimal amount) {
        String event = String.format(
                "Transfer completed: from=%s, to=%s, amount=%s",
                fromAccountId, toAccountId, amount
        );
        kafkaTemplate.send("transfers", event);
    }
}