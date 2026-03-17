package com.example.fintechledger.repository;
package com.example.fintechledger.model;

import com.example.fintechledger.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByFromAccountIdOrToAccountIdOrderByTimestampDesc(Long fromAccountId, Long toAccountId);
}


public enum TransactionStatus {
     PENDING,
     COMPLETED,
     FAILED
}
