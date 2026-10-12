
package com.example.banking_api;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    // Create a new bank account
    @PostMapping
    public ResponseEntity<Account> createAccount(
            @RequestBody Account account) {

        try {
            Account createdAccount =
                    accountService.createAccount(account);

            return ResponseEntity
                    .created(URI.create(
                            "/api/accounts/" + createdAccount.getId()))
                    .body(createdAccount);

        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    exception.getMessage(),
                    exception
            );
        }
    }

    // Get all bank accounts
    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts() {
        return ResponseEntity.ok(
                accountService.getAllAccounts()
        );
    }

    // Get an account by ID
    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccountById(
            @PathVariable Long id) {

        try {
            return ResponseEntity.ok(
                    accountService.getAccountById(id)
            );

        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    exception.getMessage(),
                    exception
            );
        }
    }

    // Get an account by account number
    @GetMapping("/number/{accountNumber}")
    public ResponseEntity<Account> getAccountByNumber(
            @PathVariable String accountNumber) {

        try {
            return ResponseEntity.ok(
                    accountService.getAccountByNumber(accountNumber)
            );

        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    exception.getMessage(),
                    exception
            );
        }
    }

    // Deposit money into an account
    @PostMapping("/{id}/deposit")
    public ResponseEntity<Account> deposit(
            @PathVariable Long id,
            @RequestBody Map<String, BigDecimal> request) {

        try {
            BigDecimal amount = request.get("amount");

            Account updatedAccount =
                    accountService.deposit(id, amount);

            return ResponseEntity.ok(updatedAccount);

        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    exception.getMessage(),
                    exception
            );
        }
    }

    // Withdraw money from an account
    @PostMapping("/{id}/withdraw")
    public ResponseEntity<Account> withdraw(
            @PathVariable Long id,
            @RequestBody Map<String, BigDecimal> request) {

        try {
            BigDecimal amount = request.get("amount");

            Account updatedAccount =
                    accountService.withdraw(id, amount);

            return ResponseEntity.ok(updatedAccount);

        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    exception.getMessage(),
                    exception
            );
        }
    }

    // Transfer money between two accounts
    @PostMapping("/transfer")
    public ResponseEntity<Map<String, String>> transfer(
            @RequestBody TransferRequest request) {

        try {
            accountService.transfer(
                    request.fromAccountId(),
                    request.toAccountId(),
                    request.amount()
            );

            return ResponseEntity.ok(Map.of(
                    "status", "SUCCESS",
                    "message", "Transfer completed successfully"
            ));

        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    exception.getMessage(),
                    exception
            );
        }
    }

    // Request data for a bank transfer
    public record TransferRequest(
            Long fromAccountId,
            Long toAccountId,
            BigDecimal amount
    ) {
    }
}
