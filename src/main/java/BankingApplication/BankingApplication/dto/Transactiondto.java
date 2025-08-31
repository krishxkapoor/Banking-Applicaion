package BankingApplication.BankingApplication.dto;

import java.math.BigDecimal;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data

public class Transactiondto {
    private String transactionType;
    private BigDecimal amount;
    private String accountNumber;
    private String status;
}
