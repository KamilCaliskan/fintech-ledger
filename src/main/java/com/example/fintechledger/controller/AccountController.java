package com.example.fintechledger.controller;

import com.example.fintechledger.model.Account;
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
        accountService.transferMoney(
            request.getFromAccountId(),
            request.getToAccountId(),
            request.getAmount()
        );
        return ResponseEntity.ok().build();
    }
}
