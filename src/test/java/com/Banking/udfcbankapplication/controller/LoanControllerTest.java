//package com.Banking.udfcbankapplication.controller;
//
//import com.Banking.udfcbankapplication.dto.LoanApplicationDTO;
//import com.Banking.udfcbankapplication.dto.LoanDTO;
//import com.Banking.udfcbankapplication.service.LoanService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.math.BigDecimal;
//import java.util.Collections;
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@SpringBootTest
//@AutoConfigureMockMvc
////@WebMvcTest(LoanController.class)
//class LoanControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private LoanService loanService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    private LoanDTO loanDTO;
//
//    @BeforeEach
//    void setup() {
//        loanDTO = new LoanDTO();
//        loanDTO.setLoanId("LOAN-20250416-0001");
//        loanDTO.setLoanAmount(new BigDecimal("1000000"));
//        loanDTO.setLoanType("Home Loan");
//    }
//
//    @Test
//    void testApplyForLoan() throws Exception {
//        LoanApplicationDTO applicationDTO = new LoanApplicationDTO();
//        applicationDTO.setCustomerId("CUST0001");
//        applicationDTO.setLoanAmount(new BigDecimal("1000000"));
//        applicationDTO.setLoanType("Home Loan");
//
//        Mockito.when(loanService.applyForLoan(any(LoanApplicationDTO.class))).thenReturn(loanDTO);
//
//        mockMvc.perform(post("/api/loans/apply")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(applicationDTO)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.loanId").value("LOAN-20250416-0001"));
//    }
//
//    @Test
//    void testGetLoansByCustomer() throws Exception {
//        List<LoanDTO> loanList = Collections.singletonList(loanDTO);
//        Mockito.when(loanService.getLoansByCustomer("CUST0001")).thenReturn(loanList);
//
//        mockMvc.perform(get("/api/loans/customer/CUST0001"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].loanId").value("LOAN-20250416-0001"));
//    }
//
//    @Test
//    void testApproveLoan() throws Exception {
//        Mockito.when(loanService.approveLoan(eq("LOAN-20250416-0001"), eq("MANAGER001"))).thenReturn(loanDTO);
//
//        mockMvc.perform(put("/api/loans/approve/LOAN-20250416-0001")
//                        .param("approvedBy", "MANAGER001"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.loanId").value("LOAN-20250416-0001"));
//    }
//
//    @Test
//    void testPayEMI() throws Exception {
//        mockMvc.perform(post("/api/loans/pay-emi/LOAN-20250416-0001")
//                        .param("amount", "15000"))
//                .andExpect(status().isOk())
//                .andExpect(content().string("EMI payment successful for loan LOAN-20250416-0001"));
//    }
//
//    @Test
//    void testForecloseLoan() throws Exception {
//        mockMvc.perform(post("/api/loans/foreclose/LOAN-20250416-0001"))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Loan LOAN-20250416-0001 foreclosed successfully"));
//    }
//}
