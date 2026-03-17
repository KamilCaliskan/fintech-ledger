package com.example.fintechledger.service;

import com.example.fintechledger.model.Transaction;
import com.example.fintechledger.model.TransactionStatus;
import com.example.fintechledger.repository.TransactionRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class TransactionLogger {
    private final TransactionRepository transactionRepository;

    public TransactionLogger(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveFailedTransaction(Long fromAccountId, Long toAccountId,
                                      BigDecimal amount, String failureReason) {
        Transaction failedTx = new Transaction(fromAccountId, toAccountId, amount,
                LocalDateTime.now(), TransactionStatus.FAILED);
        // You can extend Transaction entity to store failureReason if needed
        transactionRepository.save(failedTx);
    }
}
