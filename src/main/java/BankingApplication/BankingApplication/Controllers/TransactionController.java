package BankingApplication.BankingApplication.Controllers;

import BankingApplication.BankingApplication.entity.Transaction;
import BankingApplication.BankingApplication.service.BankStatement;
import com.itextpdf.text.DocumentException;
import java.io.FileNotFoundException;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bankStatement")
@AllArgsConstructor

public class TransactionController {
    private BankStatement bankStatement;
    public List<Transaction> generateBankStatement(@RequestParam String accountNumber,
                                                   @RequestParam String startDate,
                                                   @RequestParam String endDate ) throws FileNotFoundException, DocumentException{
        return bankStatement.generateStatement(accountNumber, startDate, endDate);
    }
}
