package BankingApplication.BankingApplication.service;

import BankingApplication.BankingApplication.dto.AccountInfo;
import BankingApplication.BankingApplication.dto.BankResponse;
import BankingApplication.BankingApplication.dto.CreditDebitRequest;
import BankingApplication.BankingApplication.dto.EmailDetails;
import BankingApplication.BankingApplication.dto.EnquiryRequest;
import BankingApplication.BankingApplication.dto.UserRequest;
import BankingApplication.BankingApplication.entity.Users;
import BankingApplication.BankingApplication.repository.Users_Repository;
import BankingApplication.BankingApplication.utils.AccountUtils;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceimpl implements UserService {

    @Autowired
    Users_Repository usersRepository;
    @Autowired
    EmailService emailService;

    @Override
    public BankResponse createAccount(UserRequest userRequest) {
        /*
        *Creating an account - Saving new user into db
        *Checking if already had acc or not
         */
        if (usersRepository.existsByEmail(userRequest.getEmail())) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        Users newUsers = Users.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .otherName(userRequest.getOtherName())
                .gender(userRequest.getGender())
                .address(userRequest.getAddress())
                .stateOfOrigin(userRequest.getStateOfOrigin())
                .accountNumber(AccountUtils.genrateAccountNumber())
                .accountBalance(BigDecimal.ZERO)
                .email(userRequest.getEmail())
                .phoneNumber(userRequest.getPhoneNumber())
                .alternativePhoneNumber(userRequest.getAlternativePhoneNumber())
                .status("ACTIVE")
                .build();

        Users savedUsers = usersRepository.save(newUsers);
        //Send Email
        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(savedUsers.getEmail())
                .subject("ACCOUNT CREATION")
                .messageBody("CONGRATULATIONS YOUR ACCOUNT HAS BEEN SUCCESSFULLY CREATED\n "
                        + "Your Account name : " + savedUsers.getFirstName()
                        + " " + savedUsers.getLastName()
                        + " " + savedUsers.getOtherName()
                        + "\nYOUR ACCOUNT NUMBER IS : "
                        + savedUsers.getAccountNumber()
                )
                .build();
        emailService.sentEmailAlerts(emailDetails);
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREATION_CODE)
                .responseMessage(AccountUtils.ACCOUNT_CREATION_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .AccountNumber(savedUsers.getAccountNumber())
                        .AccountBalance(savedUsers.getAccountBalance())
                        .AccountName(savedUsers.getFirstName() + " " + savedUsers.getLastName())
                        .build())
                .build();
    }
    //balance enquiery, name enquiry,credit,debit,transfer

    @Override
    public BankResponse balanceEnquiry(EnquiryRequest request) {
        Boolean isAccountExist = usersRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        Users foundUser = usersRepository.findByAccountNumber(request.getAccountNumber());
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
                .responseMessage(AccountUtils.ACCOUNT_FOUND_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .AccountBalance(foundUser.getAccountBalance())
                        .AccountNumber(request.getAccountNumber())
                        .AccountName(foundUser.getFirstName() + "" + foundUser.getLastName() + "" + foundUser.getOtherName())
                        .build()
                )
                .build();
    }

    @Override
    public String nameEnquiry(EnquiryRequest request) {
        Boolean isAccountExist = usersRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist) {
            return AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE;
        }
        Users foundUser = usersRepository.findByAccountNumber(request.getAccountNumber());
        return foundUser.getFirstName() + "" + foundUser.getLastName() + "" + foundUser.getOtherName();
    }

    @Override
    public BankResponse creditAccount(CreditDebitRequest request) {
        // Check if account exists
        Boolean isAccountExist = usersRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        Users userToCredit = usersRepository.findByAccountNumber(request.getAccountNumber());
        userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(request.getAmount()));
        usersRepository.save(userToCredit);
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREDITED_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_CREDITED_SUCCESS_MESSAGE)
                .accountName(userToCredit.getFirstName() + "" + userToCredit.getLastName() + "" + userToCredit.getOtherName())
                .AccountBalance(userToCredit.getAccountBalance())
                .AccountNumber(request.getAccountNumber())
                .build();
    }

    @Override
    public BankResponse debitAccount(CreditDebitRequest request) {
        //check account exists
        //check wether the amount we needed to withdraw is more than accounr balance
        Boolean isAccountExist = usersRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        Users userToDebit = usersRepository.findByAccountNumber(request.getAccountNumber());
        BigInteger availableBalance = userToDebit.getAccountBalance().toBigInteger();
        BigInteger debitAmount = request.getAmount().toBigInteger();
        if (availableBalance.intValue() < debitAmount.intValue()) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        } else {
            userToDebit.setAccountBalance(userToDebit.getAccountBalance().subtract(request.getAmount()));
            usersRepository.save(userToDebit);
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_DEBITED_SUCCESSFULLY_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_DEBITED_SUCCESSFULLY_MESSAGE)
                    .accountInfo(AccountInfo.builder()
                            .AccountNumber(request.getAccountNumber())
                            .AccountName(userToDebit.getFirstName() + "" + userToDebit.getLastName() + "" + userToDebit.getOtherName())
                            .AccountBalance(userToDebit.getAccountBalance())
                    .build())
                    .build();
        }
    }

}
