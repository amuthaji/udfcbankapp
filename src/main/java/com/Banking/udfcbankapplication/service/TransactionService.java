package com.Banking.udfcbankapplication.service;
import com.Banking.udfcbankapplication.dto.FundTransferRequestDTO;
import com.Banking.udfcbankapplication.dto.TransactionRequestDTO;
import com.Banking.udfcbankapplication.entity.Transaction;
import java.math.BigDecimal;
import java.util.List;
public interface TransactionService {
    String initiateDeposit(TransactionRequestDTO requestDTO);
    String initiateWithdrawal(TransactionRequestDTO requestDTO);
    String initiateTransfer(FundTransferRequestDTO requestDTO);
    String verifyAndProcessTransaction(String accountNumber, String otp);
    List<?> getTransactionHistory(String accountNumber);
    List<Transaction> getTransactionsByDateRange(String startDate, String endDate);
    String processATMWithdrawal(String cardNumber, BigDecimal amount, String pin);
}