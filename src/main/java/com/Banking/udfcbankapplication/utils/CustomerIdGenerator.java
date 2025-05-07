package com.Banking.udfcbankapplication.utils;
import com.Banking.udfcbankapplication.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
@Service
public class CustomerIdGenerator {
    private static final String PREFIX = "CUST";
    private static final int INITIAL_NUMBER = 1;
    private static final int ID_LENGTH = 4;
    private final CustomerRepository customerRepository;
    @Autowired
    public CustomerIdGenerator(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional
    public String generateNextCustomerId(){
        Optional<String> lastCustomerIdOpt = customerRepository.findLastCustomerId();
        int nextNumber = INITIAL_NUMBER;

        if (lastCustomerIdOpt.isPresent()) {
            String lastCustomerId = lastCustomerIdOpt.get();
            String numberPart = lastCustomerId.substring(PREFIX.length());
            nextNumber = Integer.parseInt(numberPart) + 1;
        }
        return PREFIX + String.format("%0" + ID_LENGTH + "d", nextNumber);
    }
}