package com.Banking.udfcbankapplication.entity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {
    @Id
    private String accountNumber;
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customers customer;
    private String accountType;
    private String branchName;
    private String ifscCode;
    private Boolean isActive;
    private BigDecimal balance;
    @CreationTimestamp
    private LocalDateTime createdAt;
}