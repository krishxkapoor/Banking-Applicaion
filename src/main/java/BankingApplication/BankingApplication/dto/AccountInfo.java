package BankingApplication.BankingApplication.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class AccountInfo {

    @Schema(
            name = "User Account Name"
    )
    private String AccountName;
    @Schema(
            name = "User Account Balance"
    )
    private BigDecimal AccountBalance;
    @Schema(
            name = "User Account Number"
    )
    private String AccountNumber;
}
