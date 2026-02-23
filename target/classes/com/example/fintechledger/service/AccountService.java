package com.example.fintechledger.service;

import com.example.fintechledger.repository.AccountRepository;
import com.example.fintechledger.model.Account;
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
        Account from = accountRepository.findById(fromAccountId)
                .orElseThrow(() -> new RuntimeException("From account not found"));
        Account to = accountRepository.findById(toAccountId)
                .orElseThrow(() -> new RuntimeException("To account not found"));

        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));

        accountRepository.save(from);
        accountRepository.save(to);
    }
}
