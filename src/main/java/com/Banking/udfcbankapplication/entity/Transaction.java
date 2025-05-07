package com.Banking.udfcbankapplication.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    private String transactionId;
    @ManyToOne
    @JoinColumn(name = "account_number")
    private Account account;
    private LocalDateTime transactionDate;
    private String transactionType;
    private BigDecimal deposit;
    private BigDecimal withdraw;
    private BigDecimal balance;
    private String modeOfTransaction;
    private String remarks;
    private String toAccountNumber;
    @Column(name = "status")
    private String status;
}
