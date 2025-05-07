package com.Banking.udfcbankapplication.repository;
import com.Banking.udfcbankapplication.entity.Customers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface CustomerRepository extends JpaRepository <Customers, String> {
    Optional<Customers> findByUsername(String username);
    Optional<Customers> findByName(String username);
    @Query("SELECT c.customerId FROM Customers c ORDER BY c.customerId DESC LIMIT 1")
    Optional<String> findLastCustomerId();
}
