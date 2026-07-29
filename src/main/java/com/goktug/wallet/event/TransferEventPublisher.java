package com.goktug.wallet.event;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransferEventPublisher {
    private final KafkaTemplate<String, TransferEvent> kafkaTemplate;

    public void publishTransferEvent(UUID fromAccountId, UUID toAccountId, BigDecimal amount) {
        TransferEvent event = new TransferEvent(fromAccountId,toAccountId,amount);
        kafkaTemplate.send("transfers", event);
    }
}