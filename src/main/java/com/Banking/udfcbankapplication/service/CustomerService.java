package com.Banking.udfcbankapplication.service;
import com.Banking.udfcbankapplication.dto.CustomerDTO;
import com.Banking.udfcbankapplication.entity.Customers;
import java.util.*;
public interface CustomerService {
    String registerCustomer(CustomerDTO customerDTO);
    String verifyEmail(String email, String otp);
    String addUser(Customers customers);
    List<Customers> getAllCustomers();
    Customers getCustomerByUsername(String username);
    Customers getCustomerById(String customerId);
}