package com.Banking.udfcbankapplication.ServiceImpl;
import com.Banking.udfcbankapplication.dto.LoanApplicationDTO;
import com.Banking.udfcbankapplication.dto.LoanDTO;
import com.Banking.udfcbankapplication.entity.Customers;
import com.Banking.udfcbankapplication.entity.Loan;
import com.Banking.udfcbankapplication.entity.LoanTransaction;
import com.Banking.udfcbankapplication.repository.CustomerRepository;
import com.Banking.udfcbankapplication.repository.LoanRepository;
import com.Banking.udfcbankapplication.repository.LoanTransactionRepository;
import com.Banking.udfcbankapplication.service.EmailService;
import com.Banking.udfcbankapplication.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import com.Banking.udfcbankapplication.utils.BankEnums.*;
@Service
public class LoanServiceImpl implements LoanService {
    private final LoanRepository loanRepository;
    private final CustomerRepository customerRepository;
    private final EmailService emailService;
    private final LoanTransactionRepository loanTransactionRepository;
    @Autowired
    public LoanServiceImpl(LoanRepository loanRepository,
                         CustomerRepository customerRepository,
                         EmailService emailService,
                         LoanTransactionRepository loanTransactionRepository) {
        this.loanRepository = loanRepository;
        this.customerRepository = customerRepository;
        this.emailService = emailService;
        this.loanTransactionRepository = loanTransactionRepository;
    }

    public LoanDTO applyForLoan(LoanApplicationDTO loanRequest) {
        Optional<Customers> customerOpt = customerRepository.findById(loanRequest.getCustomerId());
        if (customerOpt.isEmpty()) {
            throw new RuntimeException("Customer not found!");
        }
        Customers customer = customerOpt.get();
        BigDecimal monthlyInterest = loanRequest.getInterestRate().divide(BigDecimal.valueOf(100 * 12), 10, RoundingMode.HALF_UP);
        BigDecimal emiAmount = calculateEMI(loanRequest.getLoanAmount(), monthlyInterest, loanRequest.getTenureMonths());
        Loan loan = new Loan();loan.setLoanId(generateLoanId());loan.setCustomer(customer);loan.setLoanType(loanRequest.getLoanType());loan.setLoanAmount(loanRequest.getLoanAmount());loan.setInterestRate(loanRequest.getInterestRate());loan.setTenureMonths(loanRequest.getTenureMonths());loan.setEmiAmount(emiAmount);loan.setEmiDueDate(LocalDate.now().plusMonths(1));loan.setStatus(LoanStatus.PENDING);loan.setEndDate(LocalDate.now().plusMonths(loanRequest.getTenureMonths()));loan.setTotalPaidAmount(BigDecimal.ZERO);loan.setRemainingBalance(loanRequest.getLoanAmount());loan.setOverdueAmount(BigDecimal.ZERO);loan.setKycVerified(true);loan.setIncomeProof(loanRequest.getIncomeProof());loan.setEmploymentType(loanRequest.getEmploymentType());loan.setCibilScore(loanRequest.getCibilScore());loan.setApprovalStatus(ApprovalStatus.PENDING);loan.setOutstandingAmount(emiAmount.multiply(BigDecimal.valueOf(loanRequest.getTenureMonths())));loan.setApprovedBy(null);loan.setRemarks(LoanMessages.UNDER_REVIEW);
        Loan savedLoan = loanRepository.save(loan);
        emailService.sendLoanApplicationEmail(
                customer.getEmail(),
                customer,
                loanRequest.getLoanType(),
                loanRequest.getLoanAmount(),
                loanRequest.getTenureMonths()
        );
        return new LoanDTO(savedLoan);
    }

    private String generateLoanId() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String sequence = String.format("%04d", getNextSequenceNumber());
        return "LOAN-" + datePart + "-" + sequence;
    }

    private synchronized int getNextSequenceNumber() {
        long count = loanRepository.count() + 1;
        return (int) count;
    }

    private BigDecimal calculateEMI(BigDecimal principal, BigDecimal monthlyRate, int tenure) {
        if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
            return principal.divide(BigDecimal.valueOf(tenure), 2, RoundingMode.HALF_UP);
        }
        BigDecimal onePlusRPowerN = BigDecimal.ONE.add(monthlyRate).pow(tenure);
        BigDecimal numerator = principal.multiply(monthlyRate).multiply(onePlusRPowerN);
        BigDecimal denominator = onePlusRPowerN.subtract(BigDecimal.ONE);
        return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
    }
    public void payEMI(String loanId, BigDecimal amount) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));
        if (loan.getStatus() != LoanStatus.ACTIVE) {
            throw new RuntimeException("Loan is not active");
        }
        BigDecimal remainingBalance = loan.getRemainingBalance();
        BigDecimal outstandingAmount = loan.getOutstandingAmount();
        if (amount.compareTo(remainingBalance) > 0) {
            throw new RuntimeException("Payment exceeds remaining balance. Maximum allowed: ₹" + remainingBalance);
        }
        BigDecimal monthlyInterestRate = loan.getInterestRate().divide(BigDecimal.valueOf(1200), 10, RoundingMode.HALF_UP);
        BigDecimal interestComponent = remainingBalance.multiply(monthlyInterestRate);
        BigDecimal principalComponent = amount.subtract(interestComponent);
        if (principalComponent.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Payment is too low to cover interest. Minimum required: ₹" + interestComponent);
        }
        BigDecimal newRemainingBalance = remainingBalance.subtract(principalComponent);
        BigDecimal newOutstandingAmount = outstandingAmount.subtract(amount);
        BigDecimal emiAmount = loan.getEmiAmount();
        if (emiAmount == null) {
            throw new RuntimeException("EMI amount cannot be null for loan ID: " + loan.getLoanId());
        }
        BigDecimal paidAmount = loan.getPaidAmount();
        if (paidAmount == null) {
            paidAmount = BigDecimal.ZERO;
        }
        loan.setPaidAmount(paidAmount.add(emiAmount));
        loan.setRemainingBalance(newRemainingBalance);
        loan.setOutstandingAmount(newOutstandingAmount);
        loan.setLastPaymentDate(LocalDate.now());
        if (newRemainingBalance.compareTo(BigDecimal.ZERO) <= 0) {
            loan.setStatus(LoanStatus.CLOSED);
            loan.setLoanClosureDate(LocalDate.now());
            loan.setRemainingBalance(BigDecimal.ZERO);
            loan.setOutstandingAmount(BigDecimal.ZERO);
        }
        loanRepository.save(loan);
        LoanTransaction transaction = LoanTransaction.builder()
                .loan(loan)
                .paymentDate(LocalDateTime.now())
                .amountPaid(amount)
                .interestPaid(interestComponent)
                .principalPaid(principalComponent)
                .remainingBalance(newRemainingBalance)
                .paymentMode(LoanConstants.PAYMENT_MODE_EMI)
                .remarks(LoanConstants.EMI_REMARKS)
                .build();
        loanTransactionRepository.save(transaction);
        emailService.sendEmiPaymentEmail(loan, amount, interestComponent, principalComponent, newOutstandingAmount);
    }

    public void forecloseLoan(String loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));
        loan.setOutstandingAmount(BigDecimal.ZERO);
        loan.setStatus(LoanStatus.CLOSED);
        loan.setIsForeclosed(true);
        loanRepository.save(loan);
        emailService.sendForeclosureEmail(loan);
    }

    @Override
    public List<LoanDTO> getLoansByCustomer(String customerId) {
        List<Loan> loans = loanRepository.findByCustomer_CustomerId(customerId);
        return loans.stream().map(LoanDTO::new).toList();
    }

    public LoanDTO approveLoan(String loanId, String approvedBy) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found!"));
        loan.setStatus(LoanStatus.ACTIVE);
        loan.setApprovalStatus(ApprovalStatus.APPROVED);
        loan.setDisbursalDate(LocalDate.now());
        loan.setApprovedBy(approvedBy);
        loanRepository.save(loan);
        Customers customer = loan.getCustomer();
        String customerName = customer.getName();
        String customerEmail = customer.getEmail();
        emailService.sendLoanApprovalEmail(customerEmail, customerName, loan.getLoanId(),
                loan.getLoanAmount(), loan.getTenureMonths(), approvedBy);
        return new LoanDTO(loan);
    }
    public List<Loan> getActiveLoans() {
        return loanRepository.findByStatus(LoanStatus.ACTIVE);
    }
}