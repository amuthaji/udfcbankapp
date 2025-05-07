package com.Banking.udfcbankapplication.service;
import java.io.IOException;
import com.itextpdf.text.DocumentException;
public interface TransactionStatementService {
    byte[] generateTransactionStatement(String accountNumber, String filterType, String startDate, String endDate)
            throws IOException, DocumentException;
}


