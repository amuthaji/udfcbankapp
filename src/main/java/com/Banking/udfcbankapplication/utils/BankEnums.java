package com.Banking.udfcbankapplication.utils;

public class BankEnums {

    public enum LoanType { //this is for loan
        HOME,
        PERSONAL,
        AUTO,
        EDUCATION,
        BUSINESS
    }

    public enum EmploymentType {
        SALARIED,
        SELF_EMPLOYED,
        UNEMPLOYED,
        RETIRED,
        CONTRACTOR
    }

    public enum IncomeProofType {
        SALARY_SLIP,
        BANK_STATEMENT,
        TAX_RETURN,
        EMPLOYER_CERTIFICATE,
        OTHER
    }


    public enum LoanStatus {
        PENDING,
        ACTIVE,
        CLOSED,
        DEFAULTED
    }
    public enum ApprovalStatus {
        PENDING,
        APPROVED,
        REJECTED
    }

    public static class LoanMessages {
        public static final String UNDER_REVIEW = "Loan application under review";
    }

    public class LoanConstants {
        public static final String LOAN_NOT_FOUND = "Loan not found";
        public static final String LOAN_NOT_ACTIVE = "Loan is not active";
        public static final String PAYMENT_EXCEEDS_BALANCE = "Payment exceeds remaining balance. Maximum allowed: ₹";
        public static final String PAYMENT_TOO_LOW = "Payment is too low to cover interest. Minimum required: ₹";
        public static final String EMI_AMOUNT_NULL = "EMI amount cannot be null for loan ID: ";
        public static final String PAYMENT_MODE_EMI = "EMI_PAYMENT";
        public static final String EMI_REMARKS = "EMI Paid successfully";
    }



    public enum CardType {             //this is for ATMCard entity
        DEBIT,
        CREDIT,
        PREPAID
    }
    public enum CardBrand {
        VISA,
        MASTERCARD,
        RUPAY,
        AMEX
    }
}

















//public enum TransactionMode {
//        NEFT,
//        RTGS,
//        IMPS,
//        CASH,
//        ONLINE,
//        ATM,
//        UPI
//    }