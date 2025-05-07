package com.Banking.udfcbankapplication.controller;
import com.Banking.udfcbankapplication.service.TransactionStatementService;
import com.itextpdf.text.DocumentException;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import org.springframework.http.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@RestController
@RequestMapping("/api/transactions/statement")
public class TransactionStatementController {
    private static final Logger logger = LoggerFactory.getLogger(TransactionStatementController.class);
    private final TransactionStatementService transactionStatementService;
    @Autowired
    public TransactionStatementController(TransactionStatementService transactionStatementService) {
        this.transactionStatementService = transactionStatementService;
    }

    @Operation(summary = "Generate Transaction Statement", description = "Generates a PDF transaction statement for a given account based on specified filter criteria (with optional start and end dates).")
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_STAFF', 'ROLE_MANAGER')")
    @GetMapping("/generate")
    public ResponseEntity<byte[]> generateTransactionStatement(
            @RequestParam String accountNumber,
            @RequestParam String filterType,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        logger.info("Generating transaction statement for account: {}, filterType: {}, startDate: {}, endDate: {}",
                accountNumber, filterType, startDate, endDate);
        try {
            byte[] pdfBytes = transactionStatementService.generateTransactionStatement(accountNumber, filterType, startDate, endDate);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "Transaction_Statement.pdf");
            logger.info("Transaction statement generated successfully for account: {}", accountNumber);
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (IOException | DocumentException e) {
            logger.error("Error generating transaction statement for account: {}", accountNumber, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}