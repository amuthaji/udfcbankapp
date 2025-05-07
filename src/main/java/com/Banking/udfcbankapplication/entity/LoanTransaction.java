package com.Banking.udfcbankapplication.entity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "loan_transactions")
@Entity
public class LoanTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String transactionId;
    @ManyToOne
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;
    private BigDecimal amountPaid;
    private BigDecimal interestPaid;
    private BigDecimal principalPaid;
    private BigDecimal remainingBalance;
    private LocalDateTime paymentDate;
    private String paymentMode;
    private String remarks;
}