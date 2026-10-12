
package com.example.banking_api;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public Account createAccount(Account account) {
        if (account == null) {
            throw new IllegalArgumentException(
                "Account data is required"
            );
        }

        if (account.getAccountNumber() == null ||
                account.getAccountNumber().isBlank()) {
            throw new IllegalArgumentException(
                "Account number is required"
            );
        }

        if (account.getAccountHolder() == null ||
                account.getAccountHolder().isBlank()) {
            throw new IllegalArgumentException(
                "Account holder is required"
            );
        }

        if (accountRepository.existsByAccountNumber(
                account.getAccountNumber())) {
            throw new IllegalArgumentException(
                "Account number already exists"
            );
        }

        BigDecimal balance = account.getBalance();

        if (balance == null) {
            balance = BigDecimal.ZERO;
        }

        if (balance.signum() < 0 || balance.scale() > 2) {
            throw new IllegalArgumentException(
                "Balance must be non-negative with at most 2 decimal places"
            );
        }

        account.setId(null);
        account.setBalance(
            balance.setScale(2, RoundingMode.UNNECESSARY)
        );

        return accountRepository.save(account);
    }

    @Transactional(readOnly = true)
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Account getAccountById(Long id) {
        return accountRepository.findById(id)
            .orElseThrow(() ->
                new IllegalArgumentException(
                    "Account not found with ID: " + id
                )
            );
    }

    @Transactional(readOnly = true)
    public Account getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
            .orElseThrow(() ->
                new IllegalArgumentException(
                    "Account not found: " + accountNumber
                )
            );
    }

    // Validate transaction amounts
    private BigDecimal validateAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException(
                "Amount must be greater than zero"
            );
        }

        if (amount.scale() > 2) {
            throw new IllegalArgumentException(
                "Amount must have at most 2 decimal places"
            );
        }

        return amount.setScale(2, RoundingMode.UNNECESSARY);
    }

    // Deposit money into an account
    @Transactional
    public Account deposit(Long accountId, BigDecimal amount) {
        BigDecimal validAmount = validateAmount(amount);

        Account account = getAccountById(accountId);

        account.setBalance(
            account.getBalance().add(validAmount)
        );

        return accountRepository.save(account);
    }

    // Withdraw money from an account
    @Transactional
    public Account withdraw(Long accountId, BigDecimal amount) {
        BigDecimal validAmount = validateAmount(amount);

        Account account = getAccountById(accountId);

        if (account.getBalance().compareTo(validAmount) < 0) {
            throw new IllegalArgumentException(
                "Insufficient funds"
            );
        }

        account.setBalance(
            account.getBalance().subtract(validAmount)
        );

        return accountRepository.save(account);
    }
}
