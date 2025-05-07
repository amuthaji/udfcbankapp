package com.Banking.udfcbankapplication.ServiceImpl;
import com.Banking.udfcbankapplication.dto.FundTransferRequestDTO;
import com.Banking.udfcbankapplication.dto.TransactionRequest;
import com.Banking.udfcbankapplication.dto.TransactionRequestDTO;
import com.Banking.udfcbankapplication.entity.ATMCard;
import com.Banking.udfcbankapplication.entity.Account;
import com.Banking.udfcbankapplication.entity.Customers;
import com.Banking.udfcbankapplication.entity.Transaction;
import com.Banking.udfcbankapplication.repository.ATMCardRepository;
import com.Banking.udfcbankapplication.repository.AccountRepository;
import com.Banking.udfcbankapplication.repository.TransactionRepository;
import com.Banking.udfcbankapplication.service.AccountService;
import com.Banking.udfcbankapplication.service.EmailService;
import com.Banking.udfcbankapplication.service.TransactionService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import static com.Banking.udfcbankapplication.utils.TransactionConstants.*;

@Service
public class TransactionServiceImpl implements TransactionService {
    private static final Logger logger = LoggerFactory.getLogger(com.Banking.udfcbankapplication.service.TransactionService.class);
    private final ConcurrentHashMap<String, DailyTransactionLimit> dailyLimits = new ConcurrentHashMap<>();
    @Value("${transaction.daily.limit}")
    private BigDecimal DAILY_LIMIT;
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final AccountService accountService;
    private final EmailService emailService;
    private final Random random = new Random();
    private final ConcurrentHashMap<String, TransactionRequest> transactionRequests = new ConcurrentHashMap<>();
    private final ATMCardRepository atmCardRepository;
    @Value("${atm.card.default.pin}")
    private String defaultPin;

    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository, AccountRepository accountRepository,
                                  AccountService accountService, EmailService emailService, ATMCardRepository atmCardRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.accountService = accountService;
        this.emailService = emailService;
        this.atmCardRepository = atmCardRepository;
    }

    public String initiateDeposit(TransactionRequestDTO requestDTO) {
        String accountNumber = requestDTO.getAccountNumber();
        BigDecimal amount = requestDTO.getAmount();
        String mode = requestDTO.getModeOfTransaction();
        String remarks = requestDTO.getRemarks();
        logger.info("Initiating deposit of ₹{} to account: {}", amount, accountNumber);
        return initiateTransaction(DEPOSIT, amount, accountNumber, null, mode, remarks);
    }

    public String initiateWithdrawal(TransactionRequestDTO requestDTO) {
        String accountNumber = requestDTO.getAccountNumber();
        BigDecimal amount = requestDTO.getAmount();
        String mode = requestDTO.getModeOfTransaction();
        String remarks = requestDTO.getRemarks();
        logger.info("Initiating withdrawal of ₹{} from account: {}", amount, accountNumber);
        return initiateTransaction(WITHDRAWAL, amount, accountNumber, null, mode, remarks);
    }

    public String initiateTransfer(FundTransferRequestDTO requestDTO) {
        String accountNumber = requestDTO.getAccountNumber();
        String toAccountNumber = requestDTO.getToAccountNumber();
        BigDecimal amount = requestDTO.getAmount();
        String mode = requestDTO.getModeOfTransaction();
        String remarks = requestDTO.getRemarks();
        logger.info("Initiating transfer of ₹{} from account {} to account {}", amount, accountNumber, toAccountNumber);
        return initiateTransaction(TRANSFER, amount, accountNumber, toAccountNumber, mode, remarks);
    }

    private String initiateTransaction(String transactionType, BigDecimal amount, String accountNumber, String toAccountNumber, String modeOfTransaction, String remarks) {
        if (TRANSFER.equals(transactionType) && accountNumber.equals(toAccountNumber)) {
            logger.warn("User attempted to transfer funds to the same account: {}", accountNumber);
            return "You cannot transfer funds to your own account.";
        }

        Optional<Account> accountOpt = accountRepository.findByAccountNumber(accountNumber);
        if (accountOpt.isEmpty()) {
            logger.error("Invalid account number provided: {}", accountNumber);
            return "Invalid account number.";
        }
        Account account = accountOpt.get();
        if (!Boolean.TRUE.equals(account.getIsActive())) {
            logger.warn("Transaction attempt on inactive account: {}", accountNumber);
            return "Transaction failed. The account is inactive.";
        }
        String otp = String.format("%06d", random.nextInt(999999));
        transactionRequests.put(accountNumber, new TransactionRequest(transactionType, amount, toAccountNumber, modeOfTransaction, remarks, otp));
        logger.info("Generated OTP {} for transaction on account: {}", otp, accountNumber);
        String emailBody = "Your OTP for transaction is: " + otp;
        emailService.sendTransactionEmail(account.getCustomer().getEmail(), "Transaction OTP", emailBody);
        return "OTP sent successfully. Enter OTP to verify.";
    }

    public String verifyAndProcessTransaction(String accountNumber, String otp) {
        TransactionRequest request = transactionRequests.get(accountNumber);
        if (request == null || !request.getOtp().equals(otp)) {
            logger.warn("Invalid OTP entered for account: {}", accountNumber);
            return "Invalid OTP or transaction request expired.";
        }
        transactionRequests.remove(accountNumber);
        Optional<Account> accountOpt = accountRepository.findByAccountNumber(accountNumber);
        if (accountOpt.isEmpty()) {
            logger.error("Transaction failed: Account not found for account number: {}", accountNumber);
            return "Invalid account number.";
        }
        Account account = accountOpt.get();
        if (!Boolean.TRUE.equals(account.getIsActive())) {
            logger.warn("Transaction attempt on inactive account: {}", accountNumber);
            return "Transaction failed. The account is inactive.";
        }
        BigDecimal amount = request.getAmount();
        String transactionType = request.getTransactionType();
        TransactionMode mode = TransactionMode.valueOf(request.getModeOfTransaction().toUpperCase());
        String modeError = validateModeRules(mode, amount, transactionType);
        if (modeError != null) return modeError;
        DailyTransactionLimit limit = dailyLimits.computeIfAbsent(accountNumber, k -> new DailyTransactionLimit());
        limit.resetIfNewDay();
        if ((WITHDRAWAL.equals(transactionType) || TRANSFER.equals(transactionType))
                && limit.getTotalAmount().add(amount).compareTo(DAILY_LIMIT) > 0) {
            return "Daily transaction limit exceeded. Max limit: ₹" + DAILY_LIMIT;
        }
        String transactionId = generateTransactionId();
        logger.info("Processing transaction ID: {} for account: {}", transactionId, accountNumber);
        Transaction transaction = new Transaction();
        transaction.setTransactionId(transactionId);
        transaction.setAccount(account);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setTransactionType(request.getTransactionType());
        transaction.setModeOfTransaction(request.getModeOfTransaction());
        transaction.setRemarks(request.getRemarks());
        transaction.setToAccountNumber(request.getToAccountNumber());
        switch (request.getTransactionType()) {
            case DEPOSIT -> {
                logger.info("Depositing ₹{} to account: {}", amount, accountNumber);
                transaction.setDeposit(amount);
                transaction.setWithdraw(BigDecimal.ZERO);
                accountService.updateAccountBalance(accountNumber, amount, true);
                transaction.setBalance(account.getBalance());
                transactionRepository.save(transaction);
            }
            case WITHDRAWAL -> {
                if (account.getBalance().compareTo(amount) < 0) {
                    logger.warn("Insufficient balance in account: {}", accountNumber);
                    return "Insufficient balance.";
                }
                logger.info("Withdrawing ₹{} from account: {}", amount, accountNumber);
                transaction.setWithdraw(amount);
                transaction.setDeposit(BigDecimal.ZERO);
                accountService.updateAccountBalance(accountNumber, amount, false);
                account = accountRepository.findByAccountNumber(accountNumber).get();
                transaction.setBalance(account.getBalance());
                transactionRepository.save(transaction);
                limit.addTransaction(amount);
            }
            case TRANSFER -> {
                if (account.getBalance().compareTo(amount) < 0) {
                    logger.warn("Transfer failed due to insufficient balance in account: {}", accountNumber);
                    return "Insufficient balance.";
                }
                Optional<Account> toAccountOpt = accountRepository.findByAccountNumber(request.getToAccountNumber());
                if (toAccountOpt.isEmpty()) {
                    logger.error("Transfer failed: Invalid recipient account: {}", request.getToAccountNumber());
                    return "Invalid recipient account number.";
                }
                Account toAccount = toAccountOpt.get();
                if (!Boolean.TRUE.equals(toAccount.getIsActive())) {
                    logger.warn("Transfer attempt to inactive recipient account: {}", toAccount.getAccountNumber());
                    return "Transaction failed. The recipient account is inactive.";
                }
                logger.info("Transferring ₹{} from account {} to account {}", amount, accountNumber, toAccount.getAccountNumber());
                accountService.updateAccountBalance(accountNumber, amount, false);
                account = accountRepository.findByAccountNumber(accountNumber).get();
                transaction.setWithdraw(amount);transaction.setDeposit(BigDecimal.ZERO);transaction.setBalance(account.getBalance());accountService.updateAccountBalance(toAccount.getAccountNumber(), amount, true);toAccount = accountRepository.findByAccountNumber(toAccount.getAccountNumber()).get();Transaction receiverTransaction = new Transaction();receiverTransaction.setTransactionId(generateTransactionId());receiverTransaction.setAccount(toAccount);receiverTransaction.setTransactionDate(LocalDateTime.now());receiverTransaction.setTransactionType(TRANSFER);receiverTransaction.setModeOfTransaction(request.getModeOfTransaction());receiverTransaction.setRemarks("Received from: " + accountNumber);receiverTransaction.setToAccountNumber(accountNumber);receiverTransaction.setDeposit(amount);receiverTransaction.setWithdraw(BigDecimal.ZERO);receiverTransaction.setBalance(toAccount.getBalance());
                transactionRepository.save(transaction);
                transactionRepository.save(receiverTransaction);
                limit.addTransaction(amount);
            }
        }
        sendTransactionNotification(account, transaction);
        logger.info("Transaction successful. Transaction ID: {}", transactionId);
        return "Transaction successful. Transaction ID: " + transactionId;
    }
    static class DailyTransactionLimit {
        private BigDecimal totalAmount = BigDecimal.ZERO;
        private LocalDate lastTransactionDate = LocalDate.now();
        public void addTransaction(BigDecimal amount) {
            totalAmount = totalAmount.add(amount);
        }
        public BigDecimal getTotalAmount() {return totalAmount;}
        public void resetIfNewDay() {
            if (!lastTransactionDate.equals(LocalDate.now())) {
                totalAmount = BigDecimal.ZERO;
                lastTransactionDate = LocalDate.now();
            }
        }
    }
    private String generateTransactionId() {
        String datePart = LocalDateTime.now().toString().replaceAll("[^0-9]", "").substring(0, 8);
        int uniqueNumber = random.nextInt(9999);
        String transactionId = "UDFC-TXN-" + datePart + "-" + String.format("%04d", uniqueNumber);
        logger.info("Generated unique Transaction ID: {}", transactionId);
        return transactionId;
    }

    @Transactional
    public String processATMWithdrawal(String cardNumber, BigDecimal amount, String pin) {
        Optional<ATMCard> atmCardOpt = atmCardRepository.findByCardNumber(cardNumber);
        if (atmCardOpt.isEmpty()) {
            return "Invalid ATM card number.";
        }
        ATMCard atmCard = atmCardOpt.get();
        if (!atmCard.getIsActive()) {
            return "ATM card is inactive. Transaction denied.";
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (encoder.matches(defaultPin, atmCard.getPin())) {
            return "Please change your default PIN to make a withdrawal.";
        }
        if (!encoder.matches(pin, atmCard.getPin())) {
            return "Invalid ATM PIN. Transaction denied.";
        }
        Account account = atmCard.getAccount();
        if (account.getBalance().compareTo(amount) < 0) {
            return "Insufficient balance. Transaction failed.";
        }
        accountService.updateAccountBalance(account.getAccountNumber(), amount, false);
        Transaction transaction = new Transaction();transaction.setTransactionId(generateTransactionId());transaction.setAccount(account);transaction.setTransactionDate(LocalDateTime.now());transaction.setTransactionType(ATM_WITHDRAWAL);transaction.setWithdraw(amount);transaction.setDeposit(BigDecimal.ZERO);transaction.setBalance(account.getBalance());transaction.setModeOfTransaction(String.valueOf(TransactionMode.ATM));
        transaction.setRemarks("Withdrawal through ATM");
        transactionRepository.save(transaction);
        emailService.sendATMWithdrawalEmail(account, transaction, amount);
        return "ATM withdrawal successful. Transaction ID: " + transaction.getTransactionId();
    }

    public List<Transaction> getTransactionHistory(String accountNumber) {
        return transactionRepository.findByAccount_AccountNumberOrderByTransactionDateDesc(accountNumber);
    }

    public List<Transaction> getTransactionsByDateRange(String startDate, String endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime startDateTime = LocalDate.parse(startDate, formatter).atStartOfDay();
        LocalDateTime endDateTime = LocalDate.parse(endDate, formatter).atTime(23, 59, 59);
        return transactionRepository.findByTransactionDateBetweenOrderByTransactionDateDesc(startDateTime, endDateTime);
    }
    private void sendTransactionNotification(Account account, Transaction transaction) {
        Customers customer = account.getCustomer();
        String senderEmail = customer.getEmail();
        String senderName = customer.getName();
        BigDecimal transactionAmount = transaction.getDeposit().compareTo(BigDecimal.ZERO) > 0 ?
                transaction.getDeposit() : transaction.getWithdraw();

        if (TRANSFER.equals(transaction.getTransactionType())) {
            Optional<Account> toAccountOpt = accountRepository.findByAccountNumber(transaction.getToAccountNumber());
            if (toAccountOpt.isPresent()) {
                Account toAccount = toAccountOpt.get();
                Customers receiver = toAccount.getCustomer();
                emailService.sendTransferEmails(account, toAccount, transaction, senderName, senderEmail, receiver);
            }
        } else {
            emailService.sendTransactionAlertEmail(account, transaction, senderName, senderEmail, transactionAmount);
        }
    }

    private String validateModeRules(TransactionMode mode, BigDecimal amount, String transactionType) {
        LocalDateTime now = LocalDateTime.now();

        switch (mode) {
            case NEFT -> {
                if (now.getHour() < 8 || now.getHour() > 18) {
                    return "NEFT transactions are allowed between 8 AM and 6 PM.";
                }
                if (amount.compareTo(new BigDecimal("200000")) > 0) {
                    return "NEFT limit exceeded. Maximum per transaction: ₹2,00,000.";
                }
            }
            case RTGS -> {
                if (amount.compareTo(new BigDecimal("200000")) < 0) {
                    return "RTGS is only for amounts ₹2,00,000 and above.";
                }
                if (now.getHour() < 7 || now.getHour() > 19) {
                    return "RTGS is allowed between 7 AM and 7 PM.";
                }
            }
            case IMPS -> {
                if (amount.compareTo(new BigDecimal("500000")) > 0) {
                    return "IMPS limit exceeded. Max per transaction: ₹5,00,000.";
                }
            }
            case UPI -> {
                if (amount.compareTo(new BigDecimal("100000")) > 0) {
                    return "UPI limit exceeded. Max per Day transaction: ₹1,00,000.";
                }
            }
            case CASH -> {
                if ("DEPOSIT".equals(transactionType) && amount.compareTo(new BigDecimal("200000")) >= 0) {
                    return "Cash deposits above ₹2,00,000 require PAN details.";
                }
                if ("WITHDRAWAL".equals(transactionType) && amount.compareTo(new BigDecimal("50000")) >= 0) {
                    return "Cash withdrawals above ₹50,000 require ID verification.";
                }
            }
            case ONLINE -> {
                if (amount.compareTo(new BigDecimal("1000000")) > 0) {
                    return "Online banking limit exceeded. Max ₹10,00,000 per transaction.";
                }
            }
            case ATM -> {
                if ("WITHDRAWAL".equals(transactionType) && amount.compareTo(new BigDecimal("25000")) > 0) {
                    return "ATM withdrawal limit exceeded. Max ₹25,000 per transaction.";
                }
            }
            default -> {
                return "Invalid or unsupported mode of transaction.";
            }
        }
        return null;
    }
}