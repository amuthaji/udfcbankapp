package com.Banking.udfcbankapplication.controller;
import com.Banking.udfcbankapplication.dto.FundTransferRequestDTO;
import com.Banking.udfcbankapplication.dto.TransactionRequestDTO;
import com.Banking.udfcbankapplication.entity.Transaction;
import com.Banking.udfcbankapplication.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.math.BigDecimal;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);
    private final TransactionService transactionService;
    @Autowired
    public TransactionController(
                         TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Operation(summary = "Initiate Deposit", description = "Allows customers and staff to deposit money into an account.")
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER','ROLE_STAFF')")
    @PostMapping("/deposit")
    public String initiateDeposit(@Valid @RequestBody TransactionRequestDTO depositRequest) {
        logger.info("Initiating deposit: accountNumber={}, amount={}, mode={}, remarks={}",
                depositRequest.getAccountNumber(),
                depositRequest.getAmount(),
                depositRequest.getModeOfTransaction(),
                depositRequest.getRemarks());
        return transactionService.initiateDeposit(depositRequest);
    }

    @Operation(summary = "Initiate Withdrawal", description = "Allows customers and staff to withdraw money from an account.")
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER','ROLE_STAFF')")
    @PostMapping("/withdrawal")
    public ResponseEntity<String> initiateWithdrawal(@Valid @RequestBody TransactionRequestDTO withdrawRequest) {
        logger.info("Initiating withdrawal: accountNumber={}, amount={}, mode={}, remarks={}",
                withdrawRequest.getAccountNumber(),
                withdrawRequest.getAmount(),
                withdrawRequest.getModeOfTransaction(),
                withdrawRequest.getRemarks());
        String response = transactionService.initiateWithdrawal(withdrawRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Initiate Fund Transfer", description = "Allows customers and staff to transfer funds between accounts.")
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER','ROLE_STAFF')")
    @PostMapping("/transfer")
    public ResponseEntity<String> initiateTransfer(@Valid @RequestBody FundTransferRequestDTO transferRequest) {
        logger.info("Initiating transfer: fromAccount={}, toAccount={}, amount={}, mode={}, remarks={}",
                transferRequest.getAccountNumber(),
                transferRequest.getToAccountNumber(),
                transferRequest.getAmount(),
                transferRequest.getModeOfTransaction(),
                transferRequest.getRemarks());
        String response = transactionService.initiateTransfer(transferRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Verify Transaction with OTP", description = "Verifies a transaction using an OTP before processing it.")
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER','ROLE_STAFF')")
    @PostMapping("/verify")
    public ResponseEntity<String> verifyTransaction(@RequestParam String accountNumber,
                                                    @RequestParam String otp) {
        logger.info("Verifying transaction: accountNumber={}, otp={}", accountNumber, otp);
        try {
            String response = transactionService.verifyAndProcessTransaction(accountNumber, otp);
            logger.info("Transaction verification successful: accountNumber={}, response={}", accountNumber, response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Transaction verification failed: accountNumber={}, error={}", accountNumber, e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Transaction verification failed");
        }
    }

    @Operation(summary = "Get Transaction History", description = "Fetches the transaction history of a specific account.")
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER','ROLE_STAFF')")
    @GetMapping("/history")
    public ResponseEntity<List<?>> getTransactionHistory(@RequestParam String accountNumber) {
        logger.info("Fetching transaction history: accountNumber={}", accountNumber);
        try {
            List<?> history = transactionService.getTransactionHistory(accountNumber);
            logger.info("Transaction history fetched: accountNumber={}, transactions={}", accountNumber, history.size());
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            logger.error("Failed to fetch transaction history: accountNumber={}, error={}", accountNumber, e.getMessage(), e);
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @Operation(summary = "Get Transactions by Date Range", description = "Retrieves all transactions between a specified date range.")
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER','ROLE_STAFF')")
    @GetMapping("/history/date-range")
    public ResponseEntity<List<Transaction>> getTransactionsByDateRange(
            @RequestParam String startDate, @RequestParam String endDate) {
        logger.info("Fetching transactions by date range: startDate={}, endDate={}", startDate, endDate);
        try {
            List<Transaction> transactions = transactionService.getTransactionsByDateRange(startDate, endDate);
            logger.info("Transactions fetched: startDate={}, endDate={}, count={}", startDate, endDate, transactions.size());
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            logger.error("Failed to fetch transactions by date range: startDate={}, endDate={}, error={}", startDate, endDate, e.getMessage(), e);
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER','ROLE_STAFF')")
    @PostMapping("/atm-withdraw")
    public ResponseEntity<String> atmWithdrawal(@RequestParam String cardNumber,
                                                @RequestParam BigDecimal amount,
                                                @RequestParam String pin) {
        return ResponseEntity.ok(transactionService.processATMWithdrawal(cardNumber, amount, pin));
    }
}