package com.example.fintechledger.repository;

import com.example.fintechledger.entity.model.Account; // Adjust based on your entity package
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    // Standard CRUD methods are inherited automatically
}
