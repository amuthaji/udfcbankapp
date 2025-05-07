package com.Banking.udfcbankapplication.security;
import com.Banking.udfcbankapplication.entity.Customers;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Collections;
public class CustomerUserDetails implements UserDetails {
    private final Customers customer;
    public CustomerUserDetails(Customers customer) {
        this.customer = customer;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = customer.getRole();
        if (role == null || role.isEmpty()) {
            role = "ROLE_CUSTOMER";
        }
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }
    @Override
    public String getPassword() {
        return customer.getPassword();
    }
    @Override
    public String getUsername() {
        return customer.getUsername();
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return true;
    }
}