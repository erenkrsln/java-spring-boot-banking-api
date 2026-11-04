
package com.example.banking_api;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

// Errors are translated to HTTP responses in GlobalExceptionHandler:
// AccountNotFoundException -> 404, IllegalArgumentException -> 400.
@RestController
@RequestMapping("/api/accounts")
@Tag(name = "Accounts", description = "Manage bank accounts and transactions")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Operation(summary = "Create a new bank account")
    @PostMapping
    public ResponseEntity<Account> createAccount(
            @RequestBody Account account) {

        Account createdAccount = accountService.createAccount(account);

        return ResponseEntity
                .created(URI.create(
                        "/api/accounts/" + createdAccount.getId()))
                .body(createdAccount);
    }

    @Operation(summary = "Get all bank accounts")
    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    @Operation(summary = "Get an account by ID")
    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccountById(
            @PathVariable Long id) {

        return ResponseEntity.ok(accountService.getAccountById(id));
    }

    @Operation(summary = "Get an account by account number")
    @GetMapping("/number/{accountNumber}")
    public ResponseEntity<Account> getAccountByNumber(
            @PathVariable String accountNumber) {

        return ResponseEntity.ok(
                accountService.getAccountByNumber(accountNumber));
    }

    @Operation(summary = "Deposit money into an account")
    @PostMapping("/{id}/deposit")
    public ResponseEntity<Account> deposit(
            @PathVariable Long id,
            @RequestBody AmountRequest request) {

        return ResponseEntity.ok(
                accountService.deposit(id, request.amount()));
    }

    @Operation(summary = "Withdraw money from an account")
    @PostMapping("/{id}/withdraw")
    public ResponseEntity<Account> withdraw(
            @PathVariable Long id,
            @RequestBody AmountRequest request) {

        return ResponseEntity.ok(
                accountService.withdraw(id, request.amount()));
    }

    @Operation(summary = "Transfer money between two accounts")
    @PostMapping("/transfer")
    public ResponseEntity<Map<String, String>> transfer(
            @RequestBody TransferRequest request) {

        accountService.transfer(
                request.fromAccountId(),
                request.toAccountId(),
                request.amount()
        );

        return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "message", "Transfer completed successfully"
        ));
    }

    // Request body for deposit and withdraw
    public record AmountRequest(BigDecimal amount) {
    }

    // Request body for a bank transfer
    public record TransferRequest(
            Long fromAccountId,
            Long toAccountId,
            BigDecimal amount
    ) {
    }
}
