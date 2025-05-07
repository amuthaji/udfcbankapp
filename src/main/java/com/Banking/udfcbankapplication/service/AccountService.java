package com.Banking.udfcbankapplication.service;
import com.Banking.udfcbankapplication.dto.AccountDTO;
import org.springframework.http.ResponseEntity;
import java.math.BigDecimal;
import java.util.List;
public interface AccountService {
    ResponseEntity<AccountDTO> createAccount(AccountDTO accountDTO);
    List<AccountDTO> getAllAccounts();
    AccountDTO getAccountByCustomerId(String customerId);
    AccountDTO updateAccount(String accountNumber, Boolean isActive);
    void deleteAccount(String accountNumber);
    void updateAccountBalance(String accountNumber, BigDecimal amount, boolean isDeposit);
}