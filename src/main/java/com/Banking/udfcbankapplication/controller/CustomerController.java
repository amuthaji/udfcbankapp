package com.Banking.udfcbankapplication.controller;
import com.Banking.udfcbankapplication.dto.CustomerDTO;
import com.Banking.udfcbankapplication.entity.Customers;
import com.Banking.udfcbankapplication.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);
    private final CustomerService customerService;
    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Operation(summary = "Register a new customer", description = "Allows new customers to register with necessary details")
    @PostMapping("/register")
    public Map<String, String> registerCustomer(@Valid @RequestBody CustomerDTO customerDTO) {
        logger.info("Registering new customer: {}", customerDTO.getUsername());
        String message = customerService.registerCustomer(customerDTO);
        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        logger.info("Registration response: {}", message);
        return response;
    }

    @Operation(summary = "Verify OTP for customer email", description = "Verifies the OTP sent to the customer's email during registration")
    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, String>> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        logger.info("Verifying OTP for email: {}", email);
        String message = customerService.verifyEmail(email, otp);
        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        if (message.startsWith("Email verified successfully")) {
            logger.info("OTP verified successfully for email: {}", email);
            return ResponseEntity.ok(response);
        } else {
            logger.warn("OTP verification failed for email: {}", email);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "Retrieve all customer details", description = "Allows staff and managers to fetch details of all customers")
    @PreAuthorize("hasAnyAuthority('ROLE_MANAGER', 'ROLE_STAFF')")
    @GetMapping("/getAllCustomerdetails")
    public List<Customers> getAllCustomers() {
        logger.info("Fetching all customer details");
        return customerService.getAllCustomers();
    }

    @Operation(summary = "Retrieve customer details by username", description = "Allows staff and managers to fetch details of a specific customer using username")
    @PreAuthorize("hasAnyAuthority('ROLE_MANAGER', 'ROLE_STAFF')")
    @GetMapping("/CustomerdetailsBYusername/{username}")
    public Customers getCustomerByUsername(@PathVariable String username) {
        logger.info("Fetching customer details for username: {}", username);
        return customerService.getCustomerByUsername(username);
    }

    @Operation(summary = "Get customer by ID", description = "Retrieves customer details based on customer ID.")
    @GetMapping("/{customerId}")
    @PreAuthorize("hasAnyAuthority('ROLE_MANAGER', 'ROLE_STAFF')")
    public Customers getCustomerById(@PathVariable String customerId) {
        return customerService.getCustomerById(customerId);
    }
}