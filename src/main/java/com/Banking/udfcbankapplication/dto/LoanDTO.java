package com.Banking.udfcbankapplication.dto;
import com.Banking.udfcbankapplication.entity.Loan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import com.Banking.udfcbankapplication.utils.BankEnums.*;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoanDTO {
    private String loanId;
    private String customerId;
    private String loanType;
    private BigDecimal loanAmount;
    private BigDecimal interestRate;
    private Integer tenureMonths;
    private BigDecimal outstandingAmount;
    private BigDecimal paidAmount;
    private BigDecimal emiAmount;
    private LocalDate emiDueDate;
    private String status;
    private LocalDate disbursalDate;
    private LocalDate endDate;
    private BigDecimal totalPaidAmount;
    private BigDecimal remainingBalance;
    private BigDecimal overdueAmount;
    private LocalDate lastPaymentDate;
    private LocalDate loanClosureDate;
    private Boolean kycVerified;
    private String incomeProof;
    private String employmentType;
    private Integer cibilScore;
    private ApprovalStatus approvalStatus;
    private String approvedBy;
    private String remarks;
    private Boolean isForeclosed;

    public LoanDTO(Loan loan) {
        this.loanId = loan.getLoanId();
        this.customerId = loan.getCustomer().getCustomerId();
        this.loanType = getLoanType();
        this.loanAmount = loan.getLoanAmount();
        this.interestRate = loan.getInterestRate();
        this.tenureMonths = loan.getTenureMonths();
        this.outstandingAmount = loan.getOutstandingAmount();
        this.paidAmount = loan.getPaidAmount();
        this.emiAmount = loan.getEmiAmount();
        this.emiDueDate = loan.getEmiDueDate();
        this.status = loan.getStatus().name();
        this.disbursalDate = loan.getDisbursalDate();
        this.endDate = loan.getEndDate();
        this.totalPaidAmount = loan.getTotalPaidAmount();
        this.remainingBalance = loan.getRemainingBalance();
        this.overdueAmount = loan.getOverdueAmount();
        this.lastPaymentDate = loan.getLastPaymentDate();
        this.loanClosureDate = loan.getLoanClosureDate();
        this.kycVerified = loan.getKycVerified();
        this.incomeProof = getIncomeProof();
        this.employmentType = getEmploymentType();
        this.cibilScore = loan.getCibilScore();
        this.approvalStatus = getApprovalStatus();
        this.approvedBy = loan.getApprovedBy();
        this.remarks = loan.getRemarks();
        this.isForeclosed = loan.getIsForeclosed();
    }
}