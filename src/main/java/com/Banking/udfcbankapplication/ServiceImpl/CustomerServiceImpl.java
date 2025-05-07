package com.Banking.udfcbankapplication.ServiceImpl;
import com.Banking.udfcbankapplication.dto.CustomerDTO;
import com.Banking.udfcbankapplication.entity.Customers;
import com.Banking.udfcbankapplication.entity.OTP;
import com.Banking.udfcbankapplication.repository.CustomerRepository;
import com.Banking.udfcbankapplication.repository.OTPRepository;
import com.Banking.udfcbankapplication.utils.CustomerIdGenerator;
import com.Banking.udfcbankapplication.service.CustomerService;
import com.Banking.udfcbankapplication.service.EmailService;
import com.Banking.udfcbankapplication.utils.EncryptionUtil;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.transaction.annotation.Transactional;
import io.swagger.v3.oas.annotations.Operation;
import java.time.LocalDateTime;
import java.util.*;
import static com.Banking.udfcbankapplication.utils.RoleConstants.ROLE_CUSTOMER;
@Service
public class CustomerServiceImpl implements CustomerService {
    private static final Logger logger = LoggerFactory.getLogger(com.Banking.udfcbankapplication.service.CustomerService.class);
    private final CustomerRepository customerRepository;
    private final OTPRepository otpRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final CustomerIdGenerator customerIdGenerator;
    private final JavaMailSender mailSender;
    private final EncryptionUtil encryptionUtil;
    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository,
                         OTPRepository otpRepository,
                         BCryptPasswordEncoder passwordEncoder,
                         EmailService emailService,
                         CustomerIdGenerator customerIdGenerator,
                         JavaMailSender mailSender,
                         EncryptionUtil encryptionUtil) {
        this.customerRepository = customerRepository;
        this.otpRepository = otpRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.customerIdGenerator = customerIdGenerator;
        this.mailSender = mailSender;
        this.encryptionUtil = encryptionUtil;
    }

    private final Map<String, Customers> pendingCustomers = new HashMap<>();

    @Operation(summary = "Register a new customer", description = "Registers a new customer and sends an OTP for verification.")
    public String registerCustomer(@Valid @RequestBody CustomerDTO customerDTO) {
        logger.info("Received registration request for email: {}", customerDTO.getEmail());
        Customers customer = new Customers();customer.setName(customerDTO.getName());customer.setEmail(customerDTO.getEmail());customer.setMobileNumber(customerDTO.getMobileNumber());customer.setDob(customerDTO.getDob());customer.setAddress(customerDTO.getAddress());customer.setAadhaarNumber(encryptionUtil.encrypt(customerDTO.getAadhaarNumber()));customer.setPanNumber(encryptionUtil.encrypt(customerDTO.getPanNumber()));customer.setUsername(customerDTO.getUsername());customer.setPassword(passwordEncoder.encode(customerDTO.getPassword()));customer.setKycStatus(false);customer.setCreatedAt(LocalDateTime.now());
        customer.setIsEmailVerified(false);
        if (customer.getRole() == null || customer.getRole().isEmpty()) {
            customer.setRole(ROLE_CUSTOMER);
        }
        String otp = generateOtp();
        OTP otpEntity = new OTP(customer.getEmail(), otp, LocalDateTime.now());
        otpRepository.save(otpEntity);
        pendingCustomers.put(customer.getEmail(), customer);
        emailService.sendOtpEmail(customer.getEmail(), otp);
        logger.info("OTP sent to {} for verification.", customer.getEmail());
        return "Dear " + customer.getName() + ", an OTP has been sent to your registered email. " +
                "Please verify it to complete your UDFC Bank registration.";
    }

    @Transactional
    @Operation(summary = "Verify email with OTP", description = "Verifies the email using OTP and completes registration.")
    public String verifyEmail(String email, String otp) {
        logger.info("Verifying OTP for email: {}", email);
        OTP otpEntity = otpRepository.findById(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired OTP."));
        if (otpEntity.getExpiryTime().isBefore(LocalDateTime.now())) {
            logger.warn("OTP verification failed for {}: OTP expired.", email);
            otpRepository.deleteById(email);
            return "Invalid or expired OTP.";
        }
        if (!otpEntity.getOtp().equals(otp)) {
            logger.warn("OTP verification failed for {}: Incorrect OTP.", email);
            return "Incorrect OTP please enter correct OTP avoid attempts 3 incorrect OTP.";
        }
        Customers customer = pendingCustomers.remove(email);
        if (customer == null) {
            logger.warn("OTP verification failed for {}: No pending registration found.", email);
            return "No pending registration found for this email.";
        }
        customer.setCustomerId(customerIdGenerator.generateNextCustomerId());
        customer.setIsEmailVerified(true);
        customer.setKycStatus(true);
        customerRepository.save(customer);
        otpRepository.deleteById(email);
        emailService.sendWelcomeEmail(customer);
        logger.info("OTP verified successfully for {}. Customer ID: {}", email, customer.getCustomerId());
        return "Email verified successfully! Welcome to UDFCBANK. Your account has been registered.";
    }

    private String generateOtp() {
        return String.format("%06d", new Random().nextInt(1000000));
    }

    @Operation(summary = "Add a user", description = "Adds a new user to the database.")
    public String addUser(Customers customers) {
        logger.info("Adding new user: {}", customers.getEmail());
        customers.setPassword(passwordEncoder.encode(customers.getPassword()));
        customerRepository.save(customers);
        logger.info("User {} registered successfully.", customers.getEmail());
        return "User registered successfully!";
    }

    public Customers getCustomerById(String customerId) {
        Customers customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        customer.setAadhaarNumber(encryptionUtil.decrypt(customer.getAadhaarNumber()));
        customer.setPanNumber(encryptionUtil.decrypt(customer.getPanNumber()));
        return customer;
    }

    @Operation(summary = "Get customer by username", description = "Retrieves customer details based on username.")
    public Customers getCustomerByUsername(String username) {
        logger.info("Fetching customer details for username: {}", username);
        return customerRepository.findByUsername(username).orElse(null);
    }

    @Operation(summary = "Get all customers", description = "Retrieves a list of all customers.")
    public List<Customers> getAllCustomers() {
        logger.info("Fetching all customers from the database.");
        return customerRepository.findAll();
    }
}