package com.Banking.udfcbankapplication.dto;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import com.Banking.udfcbankapplication.utils.BankEnums.*;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanApplicationDTO {
    private String customerId;
    @Enumerated(EnumType.STRING)
    private LoanType loanType;
    private BigDecimal loanAmount;
    private BigDecimal interestRate;
    private int tenureMonths;
    private IncomeProofType incomeProof;
    private EmploymentType employmentType;
    private int cibilScore;
}