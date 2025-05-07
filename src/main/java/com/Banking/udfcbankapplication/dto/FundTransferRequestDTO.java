package com.Banking.udfcbankapplication.dto;
import lombok.*;
import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FundTransferRequestDTO  {
    private String accountNumber;
    private String toAccountNumber;
    private BigDecimal amount;
    private String modeOfTransaction;
    private String remarks;
}
