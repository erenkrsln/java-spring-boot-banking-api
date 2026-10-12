
package com.example.banking_api;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountService = new AccountService(accountRepository);
    }

    private Account createAccount(
            Long id,
            String number,
            BigDecimal balance) {

        return new Account(
                id,
                number,
                "Test Account Holder",
                balance
        );
    }

    @Test
    void shouldCreateAccount() {
        Account account = createAccount(
                null,
                "DEMO10001",
                new BigDecimal("1000.00")
        );

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
        Account account = createAccount(
                1L,
                "DEMO10001",
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
        Account account = createAccount(
                1L,
                "DEMO10001",
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
        Account account = createAccount(
                1L,
                "DEMO10001",
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

        assertEquals(
                new BigDecimal("100.00"),
                account.getBalance()
        );

        verify(accountRepository, never())
                .save(any(Account.class));
    }

    @Test
    void shouldTransferMoneyBetweenAccounts() {
        Account sender = createAccount(
                1L,
                "DEMO10001",
                new BigDecimal("1000.00")
        );

        Account receiver = createAccount(
                2L,
                "DEMO20002",
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

    @Test
    void shouldRejectTransferWithInsufficientFunds() {
        Account sender = createAccount(
                1L,
                "DEMO10001",
                new BigDecimal("100.00")
        );

        Account receiver = createAccount(
                2L,
                "DEMO20002",
                new BigDecimal("500.00")
        );

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(sender));

        when(accountRepository.findById(2L))
                .thenReturn(Optional.of(receiver));

        assertThrows(
                IllegalArgumentException.class,
                () -> accountService.transfer(
                        1L,
                        2L,
                        new BigDecimal("200.00")
                )
        );

        assertEquals(
                new BigDecimal("100.00"),
                sender.getBalance()
        );

        assertEquals(
                new BigDecimal("500.00"),
                receiver.getBalance()
        );

        verify(accountRepository, never())
                .save(any(Account.class));
    }

    @Test
    void shouldRejectTransferToSameAccount() {
        assertThrows(
                IllegalArgumentException.class,
                () -> accountService.transfer(
                        1L,
                        1L,
                        new BigDecimal("100.00")
                )
        );

        verifyNoInteractions(accountRepository);
    }

    @Test
    void shouldRejectNegativeDeposit() {
        assertThrows(
                IllegalArgumentException.class,
                () -> accountService.deposit(
                        1L,
                        new BigDecimal("-50.00")
                )
        );

        verifyNoInteractions(accountRepository);
    }

    @Test
    void shouldRejectZeroWithdrawal() {
        assertThrows(
                IllegalArgumentException.class,
                () -> accountService.withdraw(
                        1L,
                        BigDecimal.ZERO
                )
        );

        verifyNoInteractions(accountRepository);
    }

    @Test
    void shouldRejectTransferToNonExistentAccount() {
        Account sender = createAccount(
                1L,
                "DEMO10001",
                new BigDecimal("1000.00")
        );

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(sender));

        when(accountRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> accountService.transfer(
                        1L,
                        99L,
                        new BigDecimal("100.00")
                )
        );

        assertEquals(
                new BigDecimal("1000.00"),
                sender.getBalance()
        );

        verify(accountRepository, never())
                .save(any(Account.class));
    }
}
