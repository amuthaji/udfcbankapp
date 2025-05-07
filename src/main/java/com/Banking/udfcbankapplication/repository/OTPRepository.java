package com.Banking.udfcbankapplication.repository;
import com.Banking.udfcbankapplication.entity.OTP;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface OTPRepository extends JpaRepository<OTP, String> {
    Optional<OTP> findByCustomerId(String customerId);
}
