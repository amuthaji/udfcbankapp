package com.Banking.udfcbankapplication.controller;
import com.Banking.udfcbankapplication.dto.AuthRequest;
import com.Banking.udfcbankapplication.entity.StaffInfo;
import com.Banking.udfcbankapplication.security.JwtImpl;
import com.Banking.udfcbankapplication.service.StaffInfoService;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthenticationManager authenticationManager;
    private final JwtImpl jwt;
    private final StaffInfoService userInfoService;

    @Autowired
    public AuthController(
            AuthenticationManager authenticationManager,
            JwtImpl jwt,
            StaffInfoService userInfoService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwt = jwt;
        this.userInfoService = userInfoService;
    }

    @Operation(summary = "Register a New User", description = "Creates a new user and returns a confirmation message.")
    @PostMapping("/new")
    public String addNewUser(@RequestBody StaffInfo userInfo){
        logger.info("Adding new user: {}", userInfo.getName());
        return userInfoService.addUser(userInfo);
    }

    @Operation(summary = "Authenticate User", description = "Authenticates a user and returns a JWT token.")
    @PostMapping("/authenticate")
    public String authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
        logger.info("Authentication attempt for user: {}", authRequest.getUserName());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUserName(), authRequest.getPassword()));
        if (authentication.isAuthenticated()) {
            logger.info("Authentication successful for user: {}", authRequest.getUserName());
            return jwt.generateToken(authRequest.getUserName());
        } else {
            logger.warn("Authentication failed for user: {}", authRequest.getUserName());
            throw new UsernameNotFoundException("Invalid User Request");
        }
    }
}