package com.Banking.udfcbankapplication.dto;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionRequestDTO  {
    @NotBlank(message = "Account number is required")
    private String accountNumber;
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    private BigDecimal amount;
    @NotBlank(message = "Mode of transaction is required")
    private String modeOfTransaction;
    private String remarks;
}
