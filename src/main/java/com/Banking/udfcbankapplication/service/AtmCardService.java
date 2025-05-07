package com.Banking.udfcbankapplication.service;
public interface AtmCardService {
    String changeATMPin(String cardNumber, String oldPin, String newPin);
}
