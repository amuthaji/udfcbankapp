package com.Banking.udfcbankapplication.entity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import com.Banking.udfcbankapplication.utils.BankEnums.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "loans")
public class Loan {
    @Id
    @Column(name = "loan_id", unique = true, nullable = false)
    private String loanId;
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customers customer;
    @Enumerated(EnumType.STRING)
    @Column(name = "loan_type")
    private LoanType loanType;
    @Column(name = "loan_amount")
    private BigDecimal loanAmount;
    @Column(name = "interest_rate")
    private BigDecimal interestRate;
    @Column(name = "tenure_months")
    private Integer tenureMonths;
    @Column
    private BigDecimal outstandingAmount;
    @Column
    private BigDecimal paidAmount;
    @Column(name = "emi_amount")
    private BigDecimal emiAmount;
    @Column(name = "emi_due_date")
    private LocalDate emiDueDate;
    @Enumerated(EnumType.STRING)
    private LoanStatus status;
    @Column(name = "disbursal_date")
    private LocalDate disbursalDate;
    @Column(name = "end_date")
    private LocalDate endDate;
    @Column(name = "total_paid_amount")
    private BigDecimal totalPaidAmount;
    @Column(name = "remaining_balance")
    private BigDecimal remainingBalance;
    @Column(name = "overdue_amount")
    private BigDecimal overdueAmount;
    @Column(name = "last_payment_date")
    private LocalDate lastPaymentDate;
    @Column(name = "loan_closure_date")
    private LocalDate loanClosureDate;
    @Column(name = "kyc_verified")
    private Boolean kycVerified;
    @Enumerated(EnumType.STRING)
    @Column(name = "income_proof")
    private IncomeProofType incomeProof;
    @Enumerated(EnumType.STRING)
    @Column(name = "employment_type")
    private EmploymentType employmentType;
    @Column(name = "cibil_score")
    private Integer cibilScore;
    @Column(name = "approval_status")
    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus;
    @Column(name = "approved_by")
    private String approvedBy;
    @Column(name = "remarks")
    private String remarks;
    @Column
    private Boolean isForeclosed;
}
