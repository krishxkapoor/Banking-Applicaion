package BankingApplication.BankingApplication.service;

import BankingApplication.BankingApplication.dto.EmailDetails;
import BankingApplication.BankingApplication.entity.Transaction;
import BankingApplication.BankingApplication.entity.Users;
import BankingApplication.BankingApplication.repository.TransactionRepository;
import BankingApplication.BankingApplication.repository.Users_Repository;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.angus.mail.imap.ACL;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class BankStatement {

    private TransactionRepository transactionRepository;
    private Users_Repository userRepository;
    private EmailService emailService;
    private static final String FILE = "D:\\Documents\\MyStatement.pdf";

    /*
    *retrieve list of transactions within a data range and account number
    *genrate pdf file
    *send the file via email
     */
    public List<Transaction> generateStatement(String accountNumber, String startDate, String endDate) throws FileNotFoundException, DocumentException {
        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(LocalTime.MAX);
        List<Transaction> transactionList = transactionRepository.findAll().stream()
                .filter(transaction -> transaction.getAccoutNumber().equals(accountNumber))
                .filter(transaction -> !transaction.getCreatedAt().isBefore(startDateTime) && !transaction.getCreatedAt().isAfter(endDateTime))
                .toList();

        Users user = userRepository.findByAccountNumber(accountNumber);
        String customerName = user.getFirstName() + " " + user.getLastName() + " " + user.getOtherName();
        Rectangle statementSize = new Rectangle(PageSize.A4);
        Document document = new Document(statementSize);
        log.info("setting size of document");
        OutputStream outputstream = new FileOutputStream(FILE);
        PdfWriter.getInstance(document, outputstream);
        document.open();

        PdfPTable bankInfoTable = new PdfPTable(1);
        PdfPCell bankName = new PdfPCell(new Phrase("The Banking Application"));
        bankName.setBorder(0);
        bankName.setBackgroundColor(BaseColor.BLUE);
        bankName.setPadding(20f);

        PdfPCell bankAddress = new PdfPCell(new Phrase("1, Amritsar"));
        bankAddress.setBorder(0);
        bankInfoTable.addCell(bankName);
        bankInfoTable.addCell(bankAddress);

        PdfPTable statementInfo = new PdfPTable(2);
        PdfPCell customerInfo = new PdfPCell(new Phrase("Start Date: " + startDate));
        customerInfo.setBorder(0);
        PdfPCell statement = new PdfPCell(new Phrase("STATEMENT OF ACCOUNT"));
        PdfPCell stopDate = new PdfPCell(new Phrase("End Date: " + endDate));
        stopDate.setBorder(0);

        PdfPCell name = new PdfPCell(new Phrase("Customer Name : "+customerName));
        name.setBorder(0);
        PdfPCell space = new PdfPCell();
        space.setBorder(0);
        PdfPCell address = new PdfPCell(new Phrase("Customer Address : " + user.getAddress()));
        
        PdfPTable transactionTable = new PdfPTable(4) ;
        
        PdfPCell date = new PdfPCell(new Phrase("DATE"));
        date.setBackgroundColor(BaseColor.BLUE);
        date.setBorder(0);
        
        PdfPCell transactionType = new PdfPCell(new Phrase("TRANSACTION TYPE"));
        transactionType.setBackgroundColor(BaseColor.BLUE);
        transactionType.setBorder(0);
        
        PdfPCell transactionAmount = new PdfPCell(new Phrase("TRANSACTION AMOUNT"));
        transactionAmount.setBackgroundColor(BaseColor.BLUE);
        transactionAmount.setBorder(0);
        
        PdfPCell status = new PdfPCell(new Phrase("STATUS"));
        status.setBackgroundColor(BaseColor.BLUE);
        status.setBorder(0);
        
        transactionTable.addCell(date);
        transactionTable.addCell(transactionType);
        transactionTable.addCell(transactionAmount);
        transactionTable.addCell(status);
        
        transactionList.forEach(transaction -> {
            transactionTable.addCell(new Phrase(transaction.getCreatedAt().toString()));
            transactionTable.addCell(new Phrase(transaction.getTransactionType()));
            transactionTable.addCell(new Phrase(transaction.getAmount().toString()));
            transactionTable.addCell(new Phrase(transaction.getStatus()));
        });
        statementInfo.addCell(customerInfo);
        statementInfo.addCell(statement);
        statementInfo.addCell(endDate);
        statementInfo.addCell(name);
        statementInfo.addCell(space);
        statementInfo.addCell(address);
        
        document.add(bankInfoTable);
        document.add(statementInfo);
        document.add(transactionTable);
        document.close();
        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(user.getEmail())
                .subject("STATEMENT OF ACCOUNT")
                .messageBody("Kindely find your requested account attachment")
                .attachment(FILE)
                .build();
        emailService.sentEmailWithAttachment(emailDetails);
        System.out.println("statement sent");
        
        return transactionList;
    }

}
