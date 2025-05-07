package com.Banking.udfcbankapplication.controller;
import com.Banking.udfcbankapplication.dto.LoanApplicationDTO;
import com.Banking.udfcbankapplication.dto.LoanDTO;
import com.Banking.udfcbankapplication.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
@RestController
@RequestMapping("/api/loans")
public class LoanController {
    private final LoanService loanService;
    @Autowired
    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @Operation(summary = "Apply for a Loan", description = "Allows customers to apply for a loan with required details.")
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_STAFF', 'ROLE_MANAGER')")
    @PostMapping("/apply")
    public ResponseEntity<LoanDTO> applyForLoan(@RequestBody LoanApplicationDTO loanRequest) {
        LoanDTO loanDTO = loanService.applyForLoan(loanRequest);
        return ResponseEntity.ok(loanDTO);
    }

    @Operation(summary = "Get Loans by Customer", description = "Fetches all loans associated with a specific customer. Accessible by staff and managers.")
    @PreAuthorize("hasAnyAuthority('ROLE_STAFF', 'ROLE_MANAGER')")
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<LoanDTO>> getLoansByCustomer(@PathVariable String customerId) {
        List<LoanDTO> loanDTOs = loanService.getLoansByCustomer(customerId);
        return ResponseEntity.ok(loanDTOs);
    }

    @Operation(summary = "Approve a Loan", description = "Allows managers to approve a loan.")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    @PutMapping("/approve/{loanId}")
    public ResponseEntity<LoanDTO> approveLoan(@PathVariable String loanId, @RequestParam String approvedBy) {
        LoanDTO loanDTO = loanService.approveLoan(loanId, approvedBy);
        return ResponseEntity.ok(loanDTO);
    }

    @Operation(summary = "Pay EMI", description = "Allows customers to pay EMI for their loan.")
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_STAFF', 'ROLE_MANAGER')")
    @PostMapping("/pay-emi/{loanId}")
    public ResponseEntity<String> payEMI(@PathVariable String loanId, @RequestParam BigDecimal amount) {
        loanService.payEMI(loanId, amount);
        return ResponseEntity.ok("EMI payment successful for loan " + loanId);
    }

    @Operation(summary = "Foreclose a Loan", description = "Allows managers to foreclose a loan before its tenure ends.")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    @PostMapping("/foreclose/{loanId}")
    public ResponseEntity<String> forecloseLoan(@PathVariable String loanId) {
        loanService.forecloseLoan(loanId);
        return ResponseEntity.ok("Loan " + loanId + " foreclosed successfully");
    }
}