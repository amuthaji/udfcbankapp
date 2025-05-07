//package com.Banking.udfcbankapplication.ServiceImpl;
//
//import com.Banking.udfcbankapplication.dto.LoanApplicationDTO;
//import com.Banking.udfcbankapplication.dto.LoanDTO;
//import com.Banking.udfcbankapplication.entity.Customers;
//import com.Banking.udfcbankapplication.entity.Loan;
//import com.Banking.udfcbankapplication.repository.CustomerRepository;
//import com.Banking.udfcbankapplication.repository.LoanRepository;
//import com.Banking.udfcbankapplication.repository.LoanTransactionRepository;
//import com.Banking.udfcbankapplication.service.EmailService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.*;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//public class LoanServiceImplTest {
//
//    @InjectMocks
//    private LoanServiceImpl loanService;
//
//    @Mock
//    private LoanRepository loanRepository;
//
//    @Mock
//    private CustomerRepository customerRepository;
//
//    @Mock
//    private EmailService emailService;
//
//    @Mock
//    private LoanTransactionRepository loanTransactionRepository;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void testApplyForLoan_success() {
//        // Arrange
//        LoanApplicationDTO request = new LoanApplicationDTO();
//        request.setCustomerId("CUST0001");
//        request.setLoanAmount(BigDecimal.valueOf(100000));
//        request.setTenureMonths(12);
//        request.setInterestRate(BigDecimal.valueOf(10));
//        request.setLoanType("Home Loan");
//        request.setIncomeProof("Salary Slip");
//        request.setEmploymentType("Salaried");
//        request.setCibilScore(750);
//
//        Customers mockCustomer = new Customers();
//        mockCustomer.setCustomerId("CUST0001");
//        mockCustomer.setEmail("test@example.com");
//
//        when(customerRepository.findById("CUST0001")).thenReturn(Optional.of(mockCustomer));
//        when(loanRepository.save(any(Loan.class))).thenAnswer(i -> i.getArgument(0));
//
//        // Act
//        LoanDTO result = loanService.applyForLoan(request);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals("Home Loan", result.getLoanType());
//        verify(emailService).sendLoanApplicationEmail(anyString(), any(), anyString(), any(), anyInt());
//    }
//
//    @Test
//    void testPayEMI_success() {
//        // Arrange
//        Loan mockLoan = new Loan();
//        mockLoan.setLoanId("LOAN-20230401-0001");
//        mockLoan.setRemainingBalance(BigDecimal.valueOf(100000));
//        mockLoan.setEmiAmount(BigDecimal.valueOf(10000));
//        mockLoan.setStatus(Loan.LoanStatus.ACTIVE);
//
//        when(loanRepository.findById("LOAN-20230401-0001")).thenReturn(Optional.of(mockLoan));
//        when(loanRepository.save(any(Loan.class))).thenAnswer(i -> i.getArgument(0));
//
//        // Act
//        loanService.payEMI("LOAN-20230401-0001", BigDecimal.valueOf(10000));
//
//        // Assert
//        assertEquals(BigDecimal.valueOf(90000), mockLoan.getRemainingBalance());
//        verify(emailService).sendEmiPaymentEmail(any(), any(), any(), any(), any());
//    }
//
//    @Test
//    void testApproveLoan_success() {
//        // Arrange
//        Loan mockLoan = new Loan();
//        mockLoan.setLoanId("LOAN-20230401-0001");
//        mockLoan.setStatus(Loan.LoanStatus.PENDING);
//        mockLoan.setApprovalStatus(Loan.ApprovalStatus.PENDING);
//
//        Customers mockCustomer = new Customers();
//        mockCustomer.setName("John Doe");
//        mockCustomer.setEmail("john.doe@example.com");
//
//        mockLoan.setCustomer(mockCustomer);
//
//        when(loanRepository.findById("LOAN-20230401-0001")).thenReturn(Optional.of(mockLoan));
//        when(loanRepository.save(any(Loan.class))).thenAnswer(i -> i.getArgument(0));
//
//        // Act
//        LoanDTO result = loanService.approveLoan("LOAN-20230401-0001", "admin");
//
//        // Assert
//        assertEquals(Loan.LoanStatus.ACTIVE, result.getStatus());
//        verify(emailService).sendLoanApprovalEmail(anyString(), anyString(), anyString(), any(), anyInt(), anyString());
//    }
//
//    @Test
//    void testForecloseLoan_success() {
//        // Arrange
//        Loan mockLoan = new Loan();
//        mockLoan.setLoanId("LOAN-20230401-0001");
//        mockLoan.setOutstandingAmount(BigDecimal.valueOf(50000));
//        mockLoan.setStatus(Loan.LoanStatus.ACTIVE);
//        mockLoan.setIsForeclosed(false);
//
//        when(loanRepository.findById("LOAN-20230401-0001")).thenReturn(Optional.of(mockLoan));
//        when(loanRepository.save(any(Loan.class))).thenAnswer(i -> i.getArgument(0));
//
//        // Act
//        loanService.forecloseLoan("LOAN-20230401-0001");
//
//        // Assert
//        assertTrue(mockLoan.getIsForeclosed());
//        assertEquals(BigDecimal.ZERO, mockLoan.getOutstandingAmount());
//        verify(emailService).sendForeclosureEmail(any());
//    }
//
//    // Additional helper methods can be added to test other edge cases or failure conditions.
//}
