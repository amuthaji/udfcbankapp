package com.Banking.udfcbankapplication.repository;
import com.Banking.udfcbankapplication.entity.Account;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, String> {
@Modifying
@Transactional
@Query("UPDATE Account a SET a.balance = :newBalance WHERE a.accountNumber = :accountNumber")
void updateBalance(@Param("accountNumber") String accountNumber, @Param("newBalance") BigDecimal newBalance);
    Optional<Account> findFirstByCustomer_CustomerId(String customerId);
    List<Account> findByCustomer_CustomerId(String customerId);
    long countByBranchName(String branchName);
    Optional<Account> findByAccountNumber(String accountNumber);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.accountNumber = :accountNumber")
    Optional<Account> findByAccountNumberForUpdate(@Param("accountNumber") String accountNumber);
}