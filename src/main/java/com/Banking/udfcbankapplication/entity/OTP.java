package com.Banking.udfcbankapplication.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
@Entity
public class OTP {
    @Id
    private String customerId;
    private String otp;
    private LocalDateTime createdAt;
    private LocalDateTime expiryTime;

    public OTP() {
    }
    public OTP(String customerId, String otp, LocalDateTime createdAt) {
        this.customerId = customerId;
        this.otp = otp;
        this.createdAt = createdAt;
        this.expiryTime = createdAt.plusMinutes(5);
    }

    public String getCustomerId() {return customerId;}
    public void setCustomerId(String customerId) {this.customerId = customerId;}
    public String getOtp() {return otp;}
    public void setOtp(String otp) {this.otp = otp;}
    public LocalDateTime getCreatedAt() {return createdAt;}
    public void setCreatedAt(LocalDateTime createdAt) {this.createdAt = createdAt;}
    public LocalDateTime getExpiryTime() {return expiryTime;}
    public void setExpiryTime(LocalDateTime expiryTime) {this.expiryTime = expiryTime;}

    @Override
    public String toString() {
        return "OTP{" +
                "customerId='" + customerId + '\'' +
                ", otp='" + otp + '\'' +
                ", createdAt=" + createdAt +
                ", expiryTime=" + expiryTime +
                '}';
    }
}