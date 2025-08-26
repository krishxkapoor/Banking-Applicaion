package BankingApplication.BankingApplication.dto;

import BankingApplication.BankingApplication.dto.AccountInfo;
import java.math.BigDecimal;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankResponse {

    private String responseCode;
    private String responseMessage;
    private AccountInfo accountInfo;
    private String accountName;
    private BigDecimal AccountBalance;
    private String AccountNumber;

}
