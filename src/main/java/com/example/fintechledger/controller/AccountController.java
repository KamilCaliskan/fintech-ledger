package com.example.fintechledger.controller;

import com.example.fintechledger.model.Account;
import com.example.fintechledger.model.Transaction;
import com.example.fintechledger.service.AccountService;
import com.example.fintechledger.dto.TransferRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public List<Account> getAll() {
        return accountService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> getById(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.getById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Account create(@RequestBody Account account) {
        return accountService.create(account);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        accountService.delete(id);
    }

    @PostMapping("/transfer")
    public ResponseEntity<Void> transferMoney(@RequestBody TransferRequest request) {
        // Use the logging version to record failures
        accountService.transferMoneyWithLogging(
            request.getFromAccountId(),
            request.getToAccountId(),
            request.getAmount()
        );
        return ResponseEntity.ok().build();
    }

    // New endpoint: get transaction history for an account
    @GetMapping("/{id}/transactions")
    public ResponseEntity<List<Transaction>> getAccountTransactions(@PathVariable Long id) {
        List<Transaction> transactions = accountService.getTransactionsForAccount(id);
        return ResponseEntity.ok(transactions);
    }
}
