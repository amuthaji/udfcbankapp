package com.Banking.udfcbankapplication.repository;
import com.Banking.udfcbankapplication.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import com.Banking.udfcbankapplication.utils.BankEnums.*;
@Repository
public interface LoanRepository extends JpaRepository<Loan, String> {
    List<Loan> findByCustomer_CustomerId(String customerId);
    List<Loan> findByStatus(LoanStatus status);
}
