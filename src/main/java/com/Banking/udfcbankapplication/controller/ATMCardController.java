package com.Banking.udfcbankapplication.controller;
import com.Banking.udfcbankapplication.service.AtmCardService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/atm")
public class ATMCardController {
    private final AtmCardService atmCardService;
    @Autowired
    public ATMCardController(AtmCardService atmCardService) {
        this.atmCardService = atmCardService;
    }

    @Operation(summary = "Change ATM PIN", description = "Allows customers to change their ATM card PIN by providing the old and new PINs.")
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_STAFF', 'ROLE_MANAGER')")
    @PostMapping("/change-pin")
    public ResponseEntity<String> changeATMPin(@RequestParam String cardNumber, @RequestParam String oldPin, @RequestParam String newPin) {
        String response = atmCardService.changeATMPin(cardNumber, oldPin, newPin);
        return ResponseEntity.ok(response);
    }
}