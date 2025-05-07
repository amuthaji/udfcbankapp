package com.Banking.udfcbankapplication.repository;
import com.Banking.udfcbankapplication.entity.LoanTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface LoanTransactionRepository extends JpaRepository<LoanTransaction, String> {
    List<LoanTransaction> findByLoan_LoanId(String loanId);
}
