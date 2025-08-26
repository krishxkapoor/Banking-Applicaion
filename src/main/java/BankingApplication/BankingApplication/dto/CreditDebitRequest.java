package BankingApplication.BankingApplication.dto;

import java.math.BigDecimal;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditDebitRequest {
    private String AccountNumber;
    private BigDecimal Amount;
}
