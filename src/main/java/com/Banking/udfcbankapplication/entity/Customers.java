package com.Banking.udfcbankapplication.entity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDate;
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "customers")
public class Customers{
    @Id
    private String customerId;
    private String name;
    private String email;
    private String mobileNumber;
    private LocalDate dob;
    private String address;
    private String aadhaarNumber;
    private String panNumber;
    private Boolean kycStatus;
    private String username;
    @Column(name = "password")
    private String password;
    @CreationTimestamp
    private java.time.LocalDateTime createdAt;
    private Boolean isEmailVerified;
    private String role;
}