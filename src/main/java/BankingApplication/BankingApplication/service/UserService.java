package BankingApplication.BankingApplication.service;

import BankingApplication.BankingApplication.dto.BankResponse;
import BankingApplication.BankingApplication.dto.CreditDebitRequest;
import BankingApplication.BankingApplication.dto.EnquiryRequest;
import BankingApplication.BankingApplication.dto.UserRequest;

public interface UserService {
    BankResponse createAccount(UserRequest userRequest);
    BankResponse balanceEnquiry(EnquiryRequest request);
    String nameEnquiry(EnquiryRequest request);
    BankResponse creditAccount(CreditDebitRequest request);
    BankResponse debitAccount(CreditDebitRequest request);
}
