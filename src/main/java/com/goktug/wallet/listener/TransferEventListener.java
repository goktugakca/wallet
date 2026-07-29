package com.goktug.wallet.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransferEventListener {

    @KafkaListener(topics = "transfers", groupId = "wallet-group")
    public void handleTransferEvent(String event) {
        System.out.println(">>> KAFKA EVENT RECEIVED: " + event);
    }
}
