package com.Banking.udfcbankapplication.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import com.Banking.udfcbankapplication.utils.BankEnums.*;
@Entity
@Table(name = "atm_card")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ATMCard {
    @Id
    @Column(name = "card_number", length = 16, unique = true)
    private String cardNumber;
    @Column(name = "pin")
    private String pin;
    @Column(name = "cvv", length = 3)
    private String cvv;
    @Enumerated(EnumType.STRING)
    @Column(name = "card_type")
    private CardType cardType;
    @Enumerated(EnumType.STRING)
    private CardBrand cardBrand;
    @Column(name = "expiry_date")
    private LocalDate expiryDate;
    @Column(name = "is_active")
    private Boolean isActive = true;
    @OneToOne
    @JoinColumn(name = "accountNumber", referencedColumnName = "accountNumber")
    private Account account;
}