package com.Banking.udfcbankapplication.dto;
import lombok.Getter;
import java.math.BigDecimal;
@Getter
public class TransactionRequest {
    private String transactionType;
    private BigDecimal amount;
    private String toAccountNumber;
    private String modeOfTransaction;
    private String remarks;
    private String otp;

    public TransactionRequest(String transactionType, BigDecimal amount, String toAccountNumber, String modeOfTransaction, String remarks, String otp) {
        this.transactionType = transactionType;
        this.amount = amount;
        this.toAccountNumber = toAccountNumber;
        this.modeOfTransaction = modeOfTransaction;
        this.remarks = remarks;
        this.otp = otp;
    }

    public TransactionRequest() {
    }

    @Override
    public String toString() {
        return "TransactionRequest{" +
                "transactionType='" + transactionType + '\'' +
                ", amount=" + amount +
                ", toAccountNumber='" + toAccountNumber + '\'' +
                ", modeOfTransaction='" + modeOfTransaction + '\'' +
                ", remarks='" + remarks + '\'' +
                ", otp='" + otp + '\'' +
                '}';
    }
}