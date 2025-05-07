package com.Banking.udfcbankapplication.repository;
import com.Banking.udfcbankapplication.entity.StaffInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface StaffInfoRepo extends JpaRepository<StaffInfo,Integer> {
    Optional<StaffInfo> findByName(String username);
}
