package com.goktug.wallet.listener;

import com.goktug.wallet.domain.TransferAudit;
import com.goktug.wallet.event.TransferEvent;
import com.goktug.wallet.repository.TransferAuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransferEventListener {
    private final TransferAuditRepository transferAuditRepository;

    @KafkaListener(topics = "transfers", groupId = "wallet-group")
    public void handleTransferEvent(TransferEvent event) {
        TransferAudit audit = new TransferAudit();
        audit.setFromAccountId(event.fromAccountId());
        audit.setToAccountId(event.toAccountId());
        audit.setAmount(event.amount());
        transferAuditRepository.save(audit);
        System.out.println(">>> AUDIT SAVED for transfer of " + event.amount());
    }
}
