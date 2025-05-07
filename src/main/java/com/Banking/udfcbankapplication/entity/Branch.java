package com.Banking.udfcbankapplication.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Branch {
    @Id
    private String branchName;
    private String branchCode;
    private String ifscCode;
}
