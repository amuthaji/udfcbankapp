package com.Banking.udfcbankapplication.dto;
import com.Banking.udfcbankapplication.entity.Account;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
@NoArgsConstructor
public class AccountDTO {
    private String accountNumber;
    private String customerId;
    private String accountType;
    private String branchName;
    private String ifscCode;
    private BigDecimal balance;
    private Boolean isActive;
    private LocalDateTime createdAt;

    public AccountDTO(Account account) {
        this.accountNumber = account.getAccountNumber();
        this.customerId = account.getCustomer().getCustomerId();
        this.accountType = account.getAccountType();
        this.branchName = account.getBranchName();
        this.ifscCode = account.getIfscCode();
        this.balance = account.getBalance();
        this.isActive = account.getIsActive();
        this.createdAt = account.getCreatedAt();
    }
}