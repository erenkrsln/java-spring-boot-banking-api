
package com.example.banking_api;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    private AccountService accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        accountService = new AccountService(accountRepository);
    }

    @Test
    void shouldCreateAccount() {
        Account account = new Account(
            null,
            "DEMO10001",
            "Max Mustermann",
            new BigDecimal("1000.00")
        );

        when(accountRepository.existsByAccountNumber("DEMO10001"))
            .thenReturn(false);

        when(accountRepository.save(any(Account.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        Account result = accountService.createAccount(account);

        assertEquals("DEMO10001", result.getAccountNumber());
        assertEquals(
            new BigDecimal("1000.00"),
            result.getBalance()
        );

        verify(accountRepository).save(account);
    }

    @Test
    void shouldDepositMoney() {
        Account account = new Account(
            1L,
            "DEMO10001",
            "Max Mustermann",
            new BigDecimal("1000.00")
        );

        when(accountRepository.findById(1L))
            .thenReturn(Optional.of(account));

        when(accountRepository.save(any(Account.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        Account result = accountService.deposit(
            1L,
            new BigDecimal("200.00")
        );

        assertEquals(
            new BigDecimal("1200.00"),
            result.getBalance()
        );
    }

    @Test
    void shouldWithdrawMoney() {
        Account account = new Account(
            1L,
            "DEMO10001",
            "Max Mustermann",
            new BigDecimal("1000.00")
        );

        when(accountRepository.findById(1L))
            .thenReturn(Optional.of(account));

        when(accountRepository.save(any(Account.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        Account result = accountService.withdraw(
            1L,
            new BigDecimal("150.00")
        );

        assertEquals(
            new BigDecimal("850.00"),
            result.getBalance()
        );
    }

    @Test
    void shouldRejectWithdrawalWithInsufficientFunds() {
        Account account = new Account(
            1L,
            "DEMO10001",
            "Max Mustermann",
            new BigDecimal("100.00")
        );

        when(accountRepository.findById(1L))
            .thenReturn(Optional.of(account));

        assertThrows(
            IllegalArgumentException.class,
            () -> accountService.withdraw(
                1L,
                new BigDecimal("500.00")
            )
        );

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void shouldTransferMoneyBetweenAccounts() {
        Account sender = new Account(
            1L,
            "DEMO10001",
            "Max Mustermann",
            new BigDecimal("1000.00")
        );

        Account receiver = new Account(
            2L,
            "DEMO20002",
            "Erika Musterfrau",
            new BigDecimal("500.00")
        );

        when(accountRepository.findById(1L))
            .thenReturn(Optional.of(sender));

        when(accountRepository.findById(2L))
            .thenReturn(Optional.of(receiver));

        accountService.transfer(
            1L,
            2L,
            new BigDecimal("250.00")
        );

        assertEquals(
            new BigDecimal("750.00"),
            sender.getBalance()
        );

        assertEquals(
            new BigDecimal("750.00"),
            receiver.getBalance()
        );

        verify(accountRepository).save(sender);
        verify(accountRepository).save(receiver);
    }
}
