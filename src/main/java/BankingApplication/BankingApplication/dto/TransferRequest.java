package BankingApplication.BankingApplication.dto;

import java.math.BigDecimal;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferRequest {
    private String sourceAccountNumber;
    private String designationAccountNumber;
    private BigDecimal amount;
}
