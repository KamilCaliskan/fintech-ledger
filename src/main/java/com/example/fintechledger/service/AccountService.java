package com.example.fintechledger.service;

import com.example.fintechledger.model.Account;
import com.example.fintechledger.repository.AccountRepository;
import com.example.fintechledger.exception.AccountNotFoundException;
import com.example.fintechledger.exception.InsufficientBalanceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
public class AccountService {
    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public void transferMoney(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        Account from = accountRepository.findById(fromAccountId)
                .orElseThrow(() -> new AccountNotFoundException("From account not found with id: " + fromAccountId));
        Account to = accountRepository.findById(toAccountId)
                .orElseThrow(() -> new AccountNotFoundException("To account not found with id: " + toAccountId));

        if (from.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance in account: " + fromAccountId);
        }

        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));

        accountRepository.save(from);
        accountRepository.save(to);
    }
}
