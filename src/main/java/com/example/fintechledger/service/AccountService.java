package com.example.fintechledger.service;

import com.example.fintechledger.model.Account;
import com.example.fintechledger.repository.AccountRepository;
import com.example.fintechledger.exception.AccountNotFoundException;
import com.example.fintechledger.exception.InsufficientBalanceException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<Account> getAll() {
        return accountRepository.findAll();
    }

    public Account getById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + id));
    }

    public Account create(Account account) {
        return accountRepository.save(account);
    }

    @Transactional
    public void transferMoney(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        if (fromAccountId == null || toAccountId == null) {
            throw new IllegalArgumentException("Account IDs cannot be null");
        }

        if (fromAccountId.equals(toAccountId)) {
            throw new IllegalArgumentException("Cannot transfer to same account");
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        Account from = accountRepository.findById(fromAccountId)
                .orElseThrow(() -> new AccountNotFoundException("Sender not found: " + fromAccountId));

        Account to = accountRepository.findById(toAccountId)
                .orElseThrow(() -> new AccountNotFoundException("Receiver not found: " + toAccountId));

        if (from.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance in account: " + fromAccountId);
        }

        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));

        accountRepository.save(from);
        accountRepository.save(to);
    }

    public void delete(Long id) {
        accountRepository.deleteById(id);
    }
}
