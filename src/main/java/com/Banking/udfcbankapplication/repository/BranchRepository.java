package com.Banking.udfcbankapplication.repository;

import com.Banking.udfcbankapplication.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BranchRepository extends JpaRepository<Branch, String> {
    Optional<Branch> findByBranchName(String branchName);
}
