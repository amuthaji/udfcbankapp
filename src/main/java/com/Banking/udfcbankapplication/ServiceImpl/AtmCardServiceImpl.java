package com.Banking.udfcbankapplication.ServiceImpl;
import com.Banking.udfcbankapplication.entity.ATMCard;
import com.Banking.udfcbankapplication.repository.ATMCardRepository;
import com.Banking.udfcbankapplication.service.AtmCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
@Service
public class AtmCardServiceImpl implements AtmCardService {
    private final ATMCardRepository atmCardRepository;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    public AtmCardServiceImpl(ATMCardRepository atmCardRepository, PasswordEncoder passwordEncoder) {
        this.atmCardRepository = atmCardRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void saveAtmCard(ATMCard atmCard) {
        atmCardRepository.save(atmCard);
    }

    public String changeATMPin(String cardNumber, String oldPin, String newPin) {
        ATMCard atmCard = atmCardRepository.findById(cardNumber)
                .orElseThrow(() -> new RuntimeException("ATM Card not found"));
        if (!passwordEncoder.matches(oldPin, atmCard.getPin())) {
            return "Incorrect old PIN. Please try again.";
        }
        atmCard.setPin(passwordEncoder.encode(newPin));
        atmCardRepository.save(atmCard);
        return "ATM PIN changed successfully.";
    }
}