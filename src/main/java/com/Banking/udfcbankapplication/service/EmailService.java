package com.Banking.udfcbankapplication.service;
import com.Banking.udfcbankapplication.entity.Account;
import com.Banking.udfcbankapplication.entity.Customers;
import com.Banking.udfcbankapplication.entity.Loan;
import com.Banking.udfcbankapplication.entity.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.MimeMessageHelper;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import com.Banking.udfcbankapplication.utils.BankEnums.*;
@Slf4j
@Service
public class EmailService {
    private final JavaMailSender mailSender;
    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendWelcomeEmail(Customers customer) {
        String email = customer.getEmail();
        String subject = "🎉 Welcome to UDFC Bank - Account Successfully Registered!";
        String emailBody = "Dear " + customer.getName() + ",\n\n"
                + "Congratulations! Your registration with UDFC Bank is successful.\n\n"
                + "🔹 Customer ID: " + customer.getCustomerId() + "\n"
                + "🔹 Registered Email: " + email + "\n\n"
                + "You can now log in to your account using your credentials and explore our banking services.\n\n"
                + "For security purposes, please do not share your credentials with anyone.\n\n"
                + "Thank you for choosing UDFC Bank!\n"
                + "Best regards,\n"
                + "UDFC Bank Team";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(subject);
        message.setText(emailBody);
        mailSender.send(message);
    }

    public void sendTransactionEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Error sending email: " + e.getMessage());
        }
    }

    public void sendATMWithdrawalEmail(Account account, Transaction transaction, BigDecimal amount) {
        String emailSubject = "ATM Withdrawal Alert";
        String emailBody = "Dear " + account.getCustomer().getName() + ",\n\n"
                + "An ATM withdrawal of ₹" + amount + " has been made from your account (****"
                + account.getAccountNumber().substring(account.getAccountNumber().length() - 4) + ").\n"
                + "Transaction ID: " + transaction.getTransactionId() + "\n"
                + "Available Balance: ₹" + account.getBalance() + "\n\n"
                + "If you did not authorize this transaction, please contact customer support immediately.\n\n"
                + "Thank you,\nUDFC Bank";
        sendEmail(account.getCustomer().getEmail(), emailSubject, emailBody);
    }

    public void sendTransferEmails(Account senderAccount, Account receiverAccount, Transaction transaction, String senderName, String senderEmail, Customers receiver) {
        String senderSubject = "✅ Funds Transferred: ₹" + transaction.getWithdraw();
        String senderMessage = "Dear " + senderName + ",\n\n"
                + "You have successfully transferred ₹" + transaction.getWithdraw() + " to account " + maskAccountNumber(receiverAccount.getAccountNumber()) + ".\n\n"
                + "Transaction ID: " + transaction.getTransactionId() + "\n"
                + "Mode: " + transaction.getModeOfTransaction() + "\n"
                + "Remaining Balance: ₹" + senderAccount.getBalance() + "\n\n"
                + "If you did not initiate this transaction, please report it immediately by calling +918668143911 Or SMS BLOCK UPI to 8668143911.\n\n"
                + "Regards,\nUDFC Bank";
        sendEmail(senderEmail, senderSubject, senderMessage);

        String receiverSubject = "Funds Received from " + senderName;
        String receiverMessage = "Dear " + receiver.getName() + ",\n\n"
                + "You have received ₹" + transaction.getWithdraw() + " from " + senderName + " (A/C: " + maskAccountNumber(senderAccount.getAccountNumber()) + ").\n\n"
                + "Transaction ID: " + transaction.getTransactionId() + "\n"
                + "Mode: " + transaction.getModeOfTransaction() + "\n"
                + "New Balance: ₹" + receiverAccount.getBalance() + "\n\n"
                + "Regards,\nUDFC Bank";
        sendEmail(receiver.getEmail(), receiverSubject, receiverMessage);
    }
    private String maskAccountNumber(String accountNumber) {
        if (accountNumber.length() < 6) {
            return "****"; // Fallback in case of incorrect length
        }
        int len = accountNumber.length();
        return "****" + accountNumber.substring(len - 4);
    }

    public void sendTransactionAlertEmail(Account account, Transaction transaction, String senderName, String senderEmail, BigDecimal transactionAmount) {
        String subject = "❗Transaction Alert: " + transaction.getTransactionType();
        String message = "Dear " + senderName + ",\n\n"
                + "A transaction has been made on your account.\n\n"
                + "Transaction Type: " + transaction.getTransactionType() + "\n"
                + "Amount: ₹" + transactionAmount + "\n"
                + "Balance: ₹" + account.getBalance() + "\n"
                + "Mode: " + transaction.getModeOfTransaction() + "\n"
                + "Transaction ID: " + transaction.getTransactionId() + "\n\n"
                + "If you did not perform this transaction, please report it immediately by calling +918668143911 Or SMS BLOCK UPI to 8668143911.\n\n"
                + "Regards,\nUDFC Bank";
        sendEmail(senderEmail, subject, message);
    }



    public void sendEmail (String to, String subject, String body){
        System.out.println("Sending email to: " + to);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
        System.out.println("Email sent successfully.");
    }

    public void sendLoanEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }


public void sendEmailWithAttachment(String to, String subject, String text, byte[] attachmentBytes, String fileName)
        throws MessagingException {
    MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text);
        // Attach PDF
        helper.addAttachment(fileName, new org.springframework.core.io.ByteArrayResource(attachmentBytes));
        mailSender.send(message);
    }

    public void sendLowBalanceNotification(Account account) {
        Customers customer = account.getCustomer();
        String email = customer.getEmail();
        String subject = "⚠️ Low Balance Alert: Your Account";
        String message = "Dear " + customer.getName() + ",\n\n"
                + "Your account balance has dropped below ₹2000. Please deposit funds to avoid penalties.\n\n"
                + "Current Balance: ₹" + account.getBalance() + "\n\n"
                + "Regards,\nUDFC Bank";
        sendEmail(email, subject, message);
        log.info("Low balance notification sent to {}", email);
    }

    public void sendOtpEmail(String email, String otp) {
        log.info("Sending OTP email to {}", email);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("UDFC Bank - OTP Verification");
        String emailBody = "Dear Customer,\n\n"
                + "Welcome to UDFC Bank!\n\n"
                + "To complete your registration, please use the OTP below:\n"
                + "🔹 OTP: " + otp + "\n\n"
                + "This OTP is valid for 5 minutes. Please do not share it with anyone.\n\n"
                + "Thank you for choosing UDFC Bank.\n"
                + "Best regards,\n"
                + "UDFC Bank Team";

        message.setText(emailBody);
        mailSender.send(message);
        log.info("OTP email sent successfully to {}", email);
    }

    public void sendLoanApplicationEmail(String to, Customers customer, LoanType  loanType, BigDecimal loanAmount, int tenureMonths) {
        String subject = "Loan Application Received";
        String body = "Dear " + customer.getName() + ",\n\n"  // Use getFullName() instead of getName() if applicable
                + "Your loan application for ₹" + loanAmount + " has been received and is under review.\n"
                + "Loan Type: " + loanType + "\n"
                + "Tenure: " + tenureMonths + " months\n\n"
                + "You will be notified once the approval process is complete.\n\n"
                + "Thank you for banking with UDFC Bank.";

        sendEmail(to, subject, body);
    }

    public void sendLoanApprovalEmail(String to, String customerName, String loanId, BigDecimal loanAmount, int tenureMonths, String approvedBy) {
        String subject = "Loan Approved";
        String body = "Dear " + customerName + ",\n\n"
                + "🎉 Congratulations! Your loan application has been approved.\n"
                + "Loan ID: " + loanId + "\n"
                + "Loan Amount: ₹" + loanAmount + "\n"
                + "Tenure: " + tenureMonths + " months\n"
                + "Approved By: " + approvedBy + "\n\n"
                + "Please ensure timely EMI payments to maintain a good credit score.\n\n"
                + "Thank you for banking with UDFC Bank.";
        sendEmail(to, subject, body);
    }

    public void sendEmiPaymentEmail(Loan loan, BigDecimal amount, BigDecimal interestPaid, BigDecimal principalPaid, BigDecimal outstandingAmount) {
        String customerName = loan.getCustomer().getName();
        String customerEmail = loan.getCustomer().getEmail();
        String subject = "EMI Payment Successful";
        String body = "Dear " + customerName + ",\n\n"
                + "Your EMI payment of ₹" + amount + " has been successfully received for Loan ID: " + loan.getLoanId() + ".\n"
                + "Interest Paid: ₹" + interestPaid + "\n"
                + "Principal Paid: ₹" + principalPaid + "\n"
                + "Outstanding Amount: ₹" + outstandingAmount + "\n\n"
                + "Please ensure timely payments to avoid penalties.\n\n"
                + "Thank you for banking with UDFC Bank.";
        sendLoanEmail(customerEmail, subject, body);
    }
    public void sendLoansEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, false);

            mailSender.send(message);
            System.out.println("Loan email sent successfully to " + to);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    public void sendForeclosureEmail(Loan loan) {
        String emailBody = "Dear " + loan.getCustomer().getName() + ",\n\n"
                + "Your loan with Loan ID: " + loan.getLoanId() + " has been foreclosed successfully.\n"
                + "Loan Amount: ₹" + loan.getLoanAmount() + "\n"
                + "Final Settlement: ₹" + loan.getPaidAmount() + "\n\n"
                + "Thank you for banking with UDFC Bank.";

        sendLoansEmail(loan.getCustomer().getEmail(), "Loan Foreclosed Successfully", emailBody);
    }
}