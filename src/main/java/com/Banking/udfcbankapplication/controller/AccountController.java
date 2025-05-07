package com.Banking.udfcbankapplication.controller;
import com.Banking.udfcbankapplication.dto.AccountDTO;
import com.Banking.udfcbankapplication.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@Slf4j
@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    private final AccountService accountService;
    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }
    @Operation(summary = "Create a new account", description = "Allows staff and managers to create a new account for a customer.")
    @PreAuthorize("hasAnyAuthority('ROLE_MANAGER', 'ROLE_STAFF')")
    @PostMapping("/create")
    public ResponseEntity<AccountDTO> createAccount(@RequestBody AccountDTO accountDTO) {
        return accountService.createAccount(accountDTO);
    }

    @Operation(summary = "Retrieve all accounts", description = "Allows staff and managers to fetch details of all accounts.")
    @PreAuthorize("hasAnyAuthority('ROLE_MANAGER', 'ROLE_STAFF')")
    @GetMapping("/allAccountDetails")
    public List<AccountDTO> getAllAccounts() {
        log.info("Fetching all account details");
        return accountService.getAllAccounts();
    }

    @Operation(summary = "Retrieve account details by customer ID", description = "Fetches account details based on customer ID.")
    @PreAuthorize("hasAnyAuthority('ROLE_MANAGER', 'ROLE_STAFF')")
    @GetMapping("/details/{customerId}")
    public ResponseEntity<AccountDTO> getAccountByCustomerId(@PathVariable String customerId) {
        log.info("Fetching account details for customerId: {}", customerId);
        AccountDTO accountDTO = accountService.getAccountByCustomerId(customerId);
        return ResponseEntity.ok(accountDTO);
    }

    @Operation(summary = "Update account status", description = "Allows staff and managers to activate or deactivate an account.")
    @PreAuthorize("hasAnyAuthority('ROLE_MANAGER', 'ROLE_STAFF')")
    @PutMapping("/update")
    public ResponseEntity<AccountDTO> updateAccount(@RequestParam String accountNumber,
                                                    @RequestParam Boolean isActive) {
        log.info("Received request to update account | accountNumber: {}, isActive: {}", accountNumber, isActive);
        AccountDTO updatedAccountDTO = accountService.updateAccount(accountNumber, isActive);
        return ResponseEntity.ok(updatedAccountDTO);
    }

    @Operation(summary = "Deactivate an account", description = "Allows staff and managers to deactivate an account by account number.")
    @PreAuthorize("hasAnyAuthority('ROLE_MANAGER', 'ROLE_STAFF')")
    @DeleteMapping("/delete/{accountNumber}")
    public ResponseEntity<String> deleteAccount(@PathVariable String accountNumber) {
        log.info("Received request to deactivate account | accountNumber: {}", accountNumber);
        accountService.deleteAccount(accountNumber);
        log.info("Account deactivated successfully | accountNumber: {}", accountNumber);
        return ResponseEntity.ok("Account " + accountNumber + " deactivated successfully");
    }
}