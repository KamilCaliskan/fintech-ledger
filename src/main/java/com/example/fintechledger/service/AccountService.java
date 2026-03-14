package com.example.fintechledger.service;

import com.example.fintechledger.model.Account;
import com.example.fintechledger.model.Transaction;
import com.example.fintechledger.model.TransactionStatus;
import com.example.fintechledger.repository.AccountRepository;
import com.example.fintechledger.repository.TransactionRepository;
import com.example.fintechledger.exception.AccountNotFoundException;
import com.example.fintechledger.exception.InsufficientBalanceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionLogger transactionLogger;

    public AccountService(AccountRepository accountRepository,
                          TransactionRepository transactionRepository,
                          TransactionLogger transactionLogger) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.transactionLogger = transactionLogger;
    }

    // ---------- Basic CRUD ----------
    public List<Account> getAll() {
        return accountRepository.findAll();
    }

    public Account getById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + id));
    }

    public Account create(Account account) {
        return accountRepository.save(account);
    }

    public void delete(Long id) {
        Account account = getById(id);
        accountRepository.delete(account);
    }

    // ---------- Transfer ----------
    @Transactional
    public void transferMoney(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        // Validate amount
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

        // Perform transfer
        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));
        accountRepository.save(from);
        accountRepository.save(to);

        // Save SUCCESS transaction
        Transaction successTx = new Transaction(fromAccountId, toAccountId, amount,
                LocalDateTime.now(), TransactionStatus.COMPLETED);
        transactionRepository.save(successTx);
    }

    // Public method called by controller – logs failures
    public void transferMoneyWithLogging(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        try {
            transferMoney(fromAccountId, toAccountId, amount);
        } catch (Exception e) {
            transactionLogger.saveFailedTransaction(fromAccountId, toAccountId, amount, e.getMessage());
            throw e;
        }
    }

    // ---------- Transaction History ----------
    public List<Transaction> getTransactionsForAccount(Long accountId) {
        // Verify account exists (throws 404 if not)
        getById(accountId);
        return transactionRepository.findByFromAccountIdOrToAccountIdOrderByTimestampDesc(accountId, accountId);
    }
}
