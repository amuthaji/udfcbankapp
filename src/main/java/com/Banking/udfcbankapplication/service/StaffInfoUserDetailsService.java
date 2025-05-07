package com.Banking.udfcbankapplication.service;
import com.Banking.udfcbankapplication.entity.Customers;
import com.Banking.udfcbankapplication.entity.StaffInfo;
import com.Banking.udfcbankapplication.repository.CustomerRepository;
import com.Banking.udfcbankapplication.repository.StaffInfoRepo;
import com.Banking.udfcbankapplication.security.CustomerUserDetails;
import com.Banking.udfcbankapplication.security.StaffInfoUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import java.util.Optional;
@Component
public class StaffInfoUserDetailsService implements UserDetailsService {
    @Autowired
    private StaffInfoRepo userInfoRepo;
    @Autowired
    private CustomerRepository customersRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<StaffInfo> userInfo = userInfoRepo.findByName(username);
        if (userInfo.isPresent()) {
            String role = userInfo.get().getRole();
            if (isValidRole(role)) {
                return new StaffInfoUserDetails(userInfo.get());
            } else {
                throw new UsernameNotFoundException("Invalid role for user: " + username);
            }
        }
        Optional<Customers> customer = customersRepository.findByUsername(username);
        if (customer.isPresent()) {
            String role = customer.get().getRole();
            if (isValidRole(role)) {
                return new CustomerUserDetails(customer.get());
            } else {
                throw new UsernameNotFoundException("Invalid role for customer: " + username);
            }
        }
        throw new UsernameNotFoundException("User not found: " + username);
    }

    private boolean isValidRole(String role) {
        return "ROLE_CUSTOMER".equals(role) ||
                "ROLE_STAFF".equals(role) ||
                "ROLE_MANAGER".equals(role);
    }
}
