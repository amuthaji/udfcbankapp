package com.Banking.udfcbankapplication.config;
import com.Banking.udfcbankapplication.filter.JwtAuthFilter;
import com.Banking.udfcbankapplication.service.StaffInfoUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Autowired
    JwtAuthFilter authFilter;
    @Bean
    public UserDetailsService userDetailsService(){
        return new StaffInfoUserDetailsService();
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationProvider=new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers(
                        "/api/auth/**",
                        "/api/auth/login/customer",
                        "/api/auth/new",
                        "/api/auth/authenticate",
                        "/api/customers/register",
                        "/api/customers/verify-otp",
                        "/swagger-ui/**",        // Allow Swagger UI
                        "/v3/api-docs/**",       // Allow OpenAPI Docs
                        "/swagger-resources/**", // Allow Swagger resources
                        "/webjars/**"            // Allow Swagger assets
                ).permitAll()
                .requestMatchers(
                        "/api/customers/getAllCustomerdetails",
                        "/api/customers/CustomerdetailsBYusername/{username}",
                        "/api/customers/{customerId}"
                ).hasAnyAuthority("ROLE_MANAGER", "ROLE_STAFF")
                .requestMatchers(
                        "/api/accounts/create",
                        "/api/accounts/update",
                        "/api/accounts/delete/**",
                        "/api/accounts/details/{customerId}",
                        "/api/accounts/allAccountDetails"
                ).hasAnyAuthority("ROLE_MANAGER", "ROLE_STAFF")
                .requestMatchers(
                        "/api/transactions/deposit",
                        "/api/transactions/withdrawal",
                        "/api/transactions/transfer",
                        "/api/transactions/initiate",
                        "/api/transactions/atm-withdraw",
                        "/api/transactions/verify",
                        "/api/transactions/history/**",
                        "/api/transactions/history/date-range"
                ).hasAnyAuthority("ROLE_CUSTOMER","ROLE_STAFF")
                .requestMatchers(
                        "/api/transactions/statement/generate",
                        "/api/atm/change-pin"
                ).hasAnyAuthority("ROLE_CUSTOMER", "ROLE_STAFF", "ROLE_MANAGER")
                .requestMatchers(
                        "/api/loans/apply",
                        "/api/loans/pay-emi/{loanId}"
                ).hasAnyAuthority("ROLE_CUSTOMER", "ROLE_STAFF", "ROLE_MANAGER")
                .requestMatchers(
                        "/api/loans/approve/{loanId}",
                        "/api/loans/foreclose/{loanId}"
                ).hasAuthority("ROLE_MANAGER")
                .requestMatchers(
                        "/api/loans/customer/{customerId}"
                ).hasAnyAuthority("ROLE_MANAGER", "ROLE_STAFF")
                .anyRequest().authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}