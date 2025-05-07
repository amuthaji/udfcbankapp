package com.Banking.udfcbankapplication.dto;
import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
public class AuthRequest {
    private String userName;
    private String password;

    public AuthRequest() {
    }
    public AuthRequest(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }
    @Override
    public String toString() {
        return "AuthRequest{" +
                "userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}