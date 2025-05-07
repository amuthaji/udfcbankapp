package com.Banking.udfcbankapplication.repository;
import com.Banking.udfcbankapplication.entity.ATMCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface ATMCardRepository extends JpaRepository<ATMCard, String> {
    Optional<ATMCard> findByCardNumber(String cardNumber);
}
