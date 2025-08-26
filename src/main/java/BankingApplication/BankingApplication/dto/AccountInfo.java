package BankingApplication.BankingApplication.dto;

import java.math.BigDecimal;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountInfo {
    private String AccountName;
    private BigDecimal AccountBalance;
    private String AccountNumber;
}
