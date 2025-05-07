package com.Banking.udfcbankapplication.utils;

public class TransactionConstants {
    public static final String DEPOSIT = "DEPOSIT";
    public static final String WITHDRAWAL = "WITHDRAWAL";
    public static final String TRANSFER = "TRANSFER";
    public static final String ATM_WITHDRAWAL = "ATM_WITHDRAWAL";

    public enum TransactionMode {
        NEFT, RTGS, IMPS, UPI, CASH, ONLINE, ATM;
    }
}
