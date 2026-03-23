package com.example.fintechledger.service;

import com.example.fintechledger.model.Account;
import com.example.fintechledger.repository.AccountRepository;
import com.example.fintechledger.exception.AccountNotFoundException;
import com.example.fintechledger.exception.InsufficientBalanceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Testcontainers
public class AccountServiceIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();
    }

    @Test
    void transferMoney_success() {
        Account from = new Account("Alice", new BigDecimal("100.00"));
        Account to = new Account("Bob", new BigDecimal("50.00"));
        from = accountRepository.save(from);
        to = accountRepository.save(to);

        Long fromId = from.getId();
        Long toId = to.getId();
        BigDecimal amount = new BigDecimal("30.00");

        accountService.transferMoney(fromId, toId, amount);

        Account updatedFrom = accountRepository.findById(fromId).orElseThrow();
        Account updatedTo = accountRepository.findById(toId).orElseThrow();
        assertThat(updatedFrom.getBalance()).isEqualByComparingTo("70.00");
        assertThat(updatedTo.getBalance()).isEqualByComparingTo("80.00");
    }

    @Test
    void transferMoney_throwsInsufficientBalance() {
        Account from = new Account("Alice", new BigDecimal("10.00"));
        Account to = new Account("Bob", new BigDecimal("50.00"));
        from = accountRepository.save(from);
        to = accountRepository.save(to);

        Long fromId = from.getId();
        Long toId = to.getId();
        BigDecimal amount = new BigDecimal("30.00");

        assertThatThrownBy(() ->
            accountService.transferMoney(fromId, toId, amount)
        ).isInstanceOf(InsufficientBalanceException.class);
    }

    @Test
    void transferMoney_throwsAccountNotFound() {
        Account to = new Account("Bob", new BigDecimal("50.00"));
        to = accountRepository.save(to);
        Long nonExistentId = 999L;
        Long toId = to.getId();
        BigDecimal amount = new BigDecimal("10.00");

        assertThatThrownBy(() ->
            accountService.transferMoney(nonExistentId, toId, amount)
        ).isInstanceOf(AccountNotFoundException.class);

        assertThatThrownBy(() ->
            accountService.transferMoney(toId, nonExistentId, amount)
        ).isInstanceOf(AccountNotFoundException.class);
    }
}
