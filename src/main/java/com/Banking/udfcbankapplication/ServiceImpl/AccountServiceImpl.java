package com.Banking.udfcbankapplication.ServiceImpl;
import com.Banking.udfcbankapplication.dto.AccountDTO;
import com.Banking.udfcbankapplication.entity.ATMCard;
import com.Banking.udfcbankapplication.entity.Account;
import com.Banking.udfcbankapplication.entity.Customers;
import com.Banking.udfcbankapplication.repository.ATMCardRepository;
import com.Banking.udfcbankapplication.repository.AccountRepository;
import com.Banking.udfcbankapplication.repository.BranchRepository;
import com.Banking.udfcbankapplication.repository.CustomerRepository;
import com.Banking.udfcbankapplication.service.AccountService;
import com.Banking.udfcbankapplication.service.EmailService;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import com.Banking.udfcbankapplication.utils.BankEnums.*;
@Slf4j
@Service
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final EmailService emailService;
    private final ATMCardRepository atmCardRepository;
    private final PasswordEncoder passwordEncoder;
    private final BranchRepository branchRepository;
    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository,
                         CustomerRepository customerRepository,
                         EmailService emailService,
                         ATMCardRepository atmCardRepository,
                         PasswordEncoder passwordEncoder,BranchRepository branchRepository) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
        this.emailService = emailService;
        this.atmCardRepository = atmCardRepository;
        this.passwordEncoder = passwordEncoder;
        this.branchRepository = branchRepository;
    }

    @Value("${atm.card.default.pin}")
    private String defaultPin;
    @Transactional
    public ResponseEntity<AccountDTO> createAccount(AccountDTO accountDTO) {
        log.info("Entering createAccount method | customerId: {}, accountType: {}, branchName: {}, balance: {}",
                accountDTO.getCustomerId(), accountDTO.getAccountType(), accountDTO.getBranchName(), accountDTO.getBalance());
        try {
            Customers customer = customerRepository.findById(accountDTO.getCustomerId())
                    .orElseThrow(() -> {
                        log.error("Customer not found with ID: {}", accountDTO.getCustomerId());
                        return new IllegalArgumentException("Customer not found");
                    });
            String accountNumber = generateAccountNumber(accountDTO.getBranchName());
            if (accountNumber.isEmpty()) {
                log.warn("Account number generation failed for branch: {}", accountDTO.getBranchName());
                return ResponseEntity.badRequest().body(null);
            }
            Account account = new Account();account.setAccountNumber(accountNumber);account.setCustomer(customer);account.setAccountType(accountDTO.getAccountType());account.setBranchName(accountDTO.getBranchName());account.setIfscCode(getIfscCodeForBranch(accountDTO.getBranchName()));account.setIsActive(true);account.setCreatedAt(LocalDateTime.now());
            account.setBalance(accountDTO.getBalance() != null ? accountDTO.getBalance() : BigDecimal.ZERO);
            Account savedAccount = accountRepository.save(account);
            log.info("Account successfully created | accountNumber: {}", savedAccount.getAccountNumber());
            ATMCard atmCard = generateATMCard(savedAccount);
            atmCardRepository.save(atmCard);
            log.info("ATM Card generated successfully | cardNumber: {}", atmCard.getCardNumber());
            return ResponseEntity.ok(new AccountDTO(savedAccount));
        } catch (Exception e) {
            log.error("Exception occurred while creating account: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(null);
        }
    }

    private ATMCard generateATMCard(Account account) {
        ATMCard atmCard = new ATMCard();atmCard.setAccount(account);atmCard.setCardNumber(generateCardNumber());atmCard.setCardType(CardType.DEBIT);atmCard.setCardBrand(CardBrand.RUPAY);atmCard.setExpiryDate(LocalDate.now().plusYears(5));atmCard.setCvv(generateCVV());atmCard.setIsActive(true);
        atmCard.setPin(passwordEncoder.encode(defaultPin));
        return atmCard;
    }

    private String generateCardNumber() {
        Random random = new Random();
        StringBuilder cardNumber = new StringBuilder("4");
        for (int i = 0; i < 15; i++) {
            cardNumber.append(random.nextInt(10));
        }
        return cardNumber.toString();
    }

    private String generateCVV() {
        return String.format("%03d", new Random().nextInt(1000));
    }

    public List<AccountDTO> getAllAccounts() {
        log.info("Fetching all accounts from the database");
        List<AccountDTO> accountDTOs = accountRepository.findAll()
                .stream()
                .map(AccountDTO::new)
                .collect(Collectors.toList());
        log.info("Total accounts retrieved: {}", accountDTOs.size());
        return accountDTOs;
    }

    public AccountDTO getAccountByCustomerId(String customerId) {
        log.info("Fetching account details for customerId: {}", customerId);
        Account account = accountRepository.findFirstByCustomer_CustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("No account found for customer ID: " + customerId));
        return new AccountDTO(account);
    }

    @Transactional
    public AccountDTO updateAccount(String accountNumber, Boolean isActive) {
        log.info("Updating account status | accountNumber: {}, isActive: {}", accountNumber, isActive);
        Account account = accountRepository.findById(accountNumber)
                .orElseThrow(() -> {
                    log.error("Account not found | accountNumber: {}", accountNumber);
                    return new RuntimeException("Account not found");
                });
        account.setIsActive(isActive);
        return new AccountDTO(accountRepository.save(account));
    }

    @Transactional
    public void deleteAccount(String accountNumber) {
        log.info("Deactivating account | accountNumber: {}", accountNumber);
        Account account = accountRepository.findById(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        account.setIsActive(false);
        accountRepository.save(account);
        log.info("Account deactivated successfully | accountNumber: {}", accountNumber);
    }

    private String generateAccountNumber(String branchName) {
        String branchCode = getBranchCode(branchName);
        long nextNumber = accountRepository.countByBranchName(branchName) + 1;
        if (branchCode == null) {
            throw new IllegalArgumentException("Invalid branch name: " + branchName);
        }
        return branchCode + String.format("%07d", nextNumber);
    }

    @Transactional
    public void updateAccountBalance(String accountNumber, BigDecimal amount, boolean isDeposit) {
        log.info("Updating balance for account | accountNumber: {}, amount: {}, isDeposit: {}",
                accountNumber, amount, isDeposit);
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        BigDecimal newBalance = isDeposit
                ? account.getBalance().add(amount)
                : account.getBalance().subtract(amount);
        account.setBalance(newBalance);
        accountRepository.save(account);
        log.info("Balance updated successfully | accountNumber: {}, newBalance: {}",
                accountNumber, newBalance);
        if (newBalance.compareTo(new BigDecimal("2000")) < 0) {
            emailService.sendLowBalanceNotification(account);
        }
    }

    public void withdrawAmount(String accountNumber, BigDecimal withdrawAmount) {
        log.info("Processing withdrawal | accountNumber: {}, amount: {}", accountNumber, withdrawAmount);
        updateAccountBalance(accountNumber, withdrawAmount, false);
    }

    private String getBranchCode(String branchName) {
        return branchRepository.findByBranchName(branchName)
                .orElseThrow(() -> new IllegalArgumentException("Invalid branch name: " + branchName))
                .getBranchCode();
    }

    private String getIfscCodeForBranch(String branchName) {
        return branchRepository.findByBranchName(branchName)
                .orElseThrow(() -> new IllegalArgumentException("Invalid branch name: " + branchName))
                .getIfscCode();
    }
}