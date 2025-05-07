package com.Banking.udfcbankapplication.ServiceImpl;
import com.Banking.udfcbankapplication.entity.Account;
import com.Banking.udfcbankapplication.entity.Customers;
import com.Banking.udfcbankapplication.entity.Transaction;
import com.Banking.udfcbankapplication.repository.AccountRepository;
import com.Banking.udfcbankapplication.repository.TransactionRepository;
import com.Banking.udfcbankapplication.service.TransactionStatementService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
@Service
public class TransactionStatementServiceImpl implements TransactionStatementService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    @Autowired
    public TransactionStatementServiceImpl(TransactionRepository transactionRepository,
                         AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    public byte[] generateTransactionStatement(String accountNumber, String filterType, String startDate, String endDate)
            throws IOException, DocumentException {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        Customers customer = account.getCustomer();
        List<Transaction> transactions = getFilteredTransactions(accountNumber, filterType, startDate, endDate);
        return createPdfStatement(account, customer, transactions);
    }
    private List<Transaction> getFilteredTransactions(String accountNumber, String filterType, String startDate, String endDate) {
        LocalDateTime start;
        LocalDateTime end;
        LocalDate now = LocalDate.now();
        if ("MONTHLY".equalsIgnoreCase(filterType)) {
            start = now.withDayOfMonth(1).atStartOfDay();
            end = now.withDayOfMonth(now.lengthOfMonth()).atTime(23, 59, 59);
        } else if ("YEARLY".equalsIgnoreCase(filterType)) {
            start = now.withDayOfYear(1).atStartOfDay();
            end = now.withDayOfYear(now.lengthOfYear()).atTime(23, 59, 59);
        } else if ("CUSTOM".equalsIgnoreCase(filterType)) {
            if (startDate == null || endDate == null) {
                throw new IllegalArgumentException("Start date and end date must be provided for CUSTOM filter.");
            }
            start = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE).atStartOfDay();
            end = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE).atTime(23, 59, 59);
        } else {
            throw new IllegalArgumentException("Invalid filter type: " + filterType);
        }
        return transactionRepository.findTransactionsByDateRange(accountNumber, start, end);
    }
    private byte[] createPdfStatement(Account account, Customers customer, List<Transaction> transactions) throws DocumentException, IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, outputStream);
        document.open();
        Font boldFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
        Paragraph title = new Paragraph("Transaction Statement", new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD));
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph("\n"));
        document.add(new Paragraph("Customer Name  : " + customer.getName(), boldFont));
        document.add(new Paragraph("Customer ID    : " + customer.getCustomerId(), normalFont));
        document.add(new Paragraph("Mobile Number  : " + customer.getMobileNumber(), normalFont));
        document.add(new Paragraph("Email          : " + customer.getEmail(), normalFont));
        document.add(new Paragraph("\n"));
        document.add(new Paragraph("Account Number : " + account.getAccountNumber(), boldFont));
        document.add(new Paragraph("IFSC Code      : " + account.getIfscCode(), normalFont));
        document.add(new Paragraph("Branch Name    : " + account.getBranchName(), normalFont));
        document.add(new Paragraph("Balance        : ₹" + account.getBalance(), normalFont));
        document.add(new Paragraph("Statement Date : " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")), normalFont));
        document.add(new Paragraph("\n"));
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);
        addTableHeader(table, "Date");
        addTableHeader(table, "Transaction ID");
        addTableHeader(table, "Type");
        addTableHeader(table, "Withdraw");
        addTableHeader(table, "Deposit");
        addTableHeader(table, "Balance");
        for (Transaction txn : transactions) {
            table.addCell(new PdfPCell(new Phrase(txn.getTransactionDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")), normalFont)));
            table.addCell(new PdfPCell(new Phrase(txn.getTransactionId(), normalFont)));
            table.addCell(new PdfPCell(new Phrase(txn.getModeOfTransaction(), normalFont)));
            table.addCell(new PdfPCell(new Phrase(txn.getWithdraw() != null ? txn.getWithdraw().toString() : "-", normalFont)));
            table.addCell(new PdfPCell(new Phrase(txn.getDeposit() != null ? txn.getDeposit().toString() : "-", normalFont)));
            table.addCell(new PdfPCell(new Phrase(txn.getBalance().toString(), normalFont)));
        }
        document.add(table);
        document.close();
        return outputStream.toByteArray();
    }
    private void addTableHeader(PdfPTable table, String columnTitle) {
        PdfPCell header = new PdfPCell();
        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
        header.setPhrase(new Phrase(columnTitle, new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD)));
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(header);
    }
}