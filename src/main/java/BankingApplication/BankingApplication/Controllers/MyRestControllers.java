package BankingApplication.BankingApplication.Controllers;

import BankingApplication.BankingApplication.dto.BankResponse;
import BankingApplication.BankingApplication.dto.CreditDebitRequest;
import BankingApplication.BankingApplication.dto.EnquiryRequest;
import BankingApplication.BankingApplication.dto.TransferRequest;
import BankingApplication.BankingApplication.dto.UserRequest;
import BankingApplication.BankingApplication.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@Tag(
        name = "User Account Management API"
)
public class MyRestControllers {

    @Autowired
    UserService userService;

    @Operation(
            description = "Creating a new user assigning an account ID",
            summary = "Creating new account "
    )
    @ApiResponse(
            responseCode = "201",
            description = "Http status 201 CREATED"
    )

    @PostMapping
    public BankResponse createAccount(@RequestBody UserRequest userRequest) {
        return userService.createAccount(userRequest);
    }
    @Operation(
            summary = "Balance Enquiry",
            description = "Giving an account number, Check how much the user has"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http status 200 SUCCESS"
    )
    @GetMapping("/balanceEnquiry")
    public BankResponse balanceEnquiry(@RequestBody EnquiryRequest request) {
        return userService.balanceEnquiry(request);
    }

    @GetMapping("/nameEnquiry")
    public String nameEnquiry(@RequestBody EnquiryRequest request) {
        return userService.nameEnquiry(request);
    }

    @PostMapping("credit")
    public BankResponse creditAccount(@RequestBody CreditDebitRequest request) {
        return userService.creditAccount(request);
    }

    @PostMapping("debit")
    public BankResponse debitAccount(@RequestBody CreditDebitRequest request) {
        return userService.debitAccount(request);
    }

    @PostMapping("transfer")
    public BankResponse transfer(@RequestBody TransferRequest request) {
        return userService.transfer(request);
    }
}
