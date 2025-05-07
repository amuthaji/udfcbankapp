package com.Banking.udfcbankapplication.service;
import com.Banking.udfcbankapplication.dto.LoanApplicationDTO;
import com.Banking.udfcbankapplication.dto.LoanDTO;
import java.math.BigDecimal;
import java.util.List;
public interface LoanService {
    LoanDTO applyForLoan(LoanApplicationDTO loanRequest);
    List<LoanDTO> getLoansByCustomer(String customerId);
    LoanDTO approveLoan(String loanId, String approvedBy);
    void payEMI(String loanId, BigDecimal amount);
    void forecloseLoan(String loanId);
}
