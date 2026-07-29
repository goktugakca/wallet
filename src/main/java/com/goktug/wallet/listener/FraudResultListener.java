package com.goktug.wallet.listener;

import com.goktug.wallet.domain.FlaggedTransfer;
import com.goktug.wallet.event.FraudResult;
import com.goktug.wallet.repository.FlaggedTransferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FraudResultListener {
    private final FlaggedTransferRepository flaggedTransferRepository;
    @KafkaListener(topics = "fraud-results", groupId = "wallet-group")
    public void handleFraudResult(FraudResult result) {
        System.out.println(">>> FRAUD RESULT RECEIVED: score=" + result.score() + " suspicious=" + result.suspicious());

        if (result.suspicious()) {
            FlaggedTransfer flagged = new FlaggedTransfer();
            flagged.setFromAccountId(result.fromAccountId());
            flagged.setToAccountId(result.toAccountId());
            flagged.setAmount(result.amount());
            flagged.setScore(result.score());
            flaggedTransferRepository.save(flagged);
            System.out.println(">>> TRANSFER FLAGGED as suspicious");
        }
    }
}
