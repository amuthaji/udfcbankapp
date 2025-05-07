package com.Banking.udfcbankapplication.repository;
import com.Banking.udfcbankapplication.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {
    List<Transaction> findByAccount_AccountNumberOrderByTransactionDateDesc(String accountNumber);
    List<Transaction> findByTransactionDateBetweenOrderByTransactionDateDesc(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT SUM(t.withdraw) FROM Transaction t WHERE t.account.accountNumber = :accountNumber AND t.transactionDate BETWEEN :startOfDay AND :endOfDay")
    Optional<BigDecimal> sumWithdrawalsForAccountBetweenDates(@Param("accountNumber") String accountNumber,
                                                              @Param("startOfDay") LocalDateTime startOfDay,
                                                              @Param("endOfDay") LocalDateTime endOfDay);
    @Query("SELECT SUM(t.withdraw) FROM Transaction t WHERE t.account.accountNumber = :accountNumber AND t.toAccountNumber IS NOT NULL AND t.transactionDate BETWEEN :startOfDay AND :endOfDay")
    Optional<BigDecimal> sumTransfersForAccountBetweenDates(@Param("accountNumber") String accountNumber,
                                                            @Param("startOfDay") LocalDateTime startOfDay,
                                                            @Param("endOfDay") LocalDateTime endOfDay);
    @Query("SELECT t FROM Transaction t WHERE t.account.accountNumber = :accountNumber " +
        "AND t.transactionDate BETWEEN :start AND :end ORDER BY t.transactionDate DESC")
    List<Transaction> findTransactionsByDateRange(@Param("accountNumber") String accountNumber,
                                              @Param("start") LocalDateTime start,
                                              @Param("end") LocalDateTime end);
}
