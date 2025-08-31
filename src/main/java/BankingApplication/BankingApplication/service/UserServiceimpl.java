package BankingApplication.BankingApplication.service;

import BankingApplication.BankingApplication.dto.AccountInfo;
import BankingApplication.BankingApplication.dto.BankResponse;
import BankingApplication.BankingApplication.dto.CreditDebitRequest;
import BankingApplication.BankingApplication.dto.EmailDetails;
import BankingApplication.BankingApplication.dto.EnquiryRequest;
import BankingApplication.BankingApplication.dto.Transactiondto;
import BankingApplication.BankingApplication.dto.TransferRequest;
import BankingApplication.BankingApplication.dto.UserRequest;
import BankingApplication.BankingApplication.entity.Users;
import BankingApplication.BankingApplication.repository.Users_Repository;
import BankingApplication.BankingApplication.utils.AccountUtils;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceimpl implements UserService {

    @Autowired
    Users_Repository usersRepository;

    @Autowired
    EmailService emailService;

    @Autowired
    TransactionService transactionService;

    private String getFullName(Users user) {
        return Stream.of(user.getFirstName(), user.getLastName(), user.getOtherName())
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" "));
    }

    @Override
    public BankResponse createAccount(UserRequest userRequest) {
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

        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(savedUsers.getEmail())
                .subject("ACCOUNT CREATION")
                .messageBody("CONGRATULATIONS YOUR ACCOUNT HAS BEEN SUCCESSFULLY CREATED\n "
                        + "Your Account name : " + getFullName(savedUsers)
                        + "\nYOUR ACCOUNT NUMBER IS : " + savedUsers.getAccountNumber()
                )
                .build();

        emailService.sentEmailAlerts(emailDetails);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREATION_CODE)
                .responseMessage(AccountUtils.ACCOUNT_CREATION_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .AccountNumber(savedUsers.getAccountNumber())
                        .AccountBalance(savedUsers.getAccountBalance())
                        .AccountName(getFullName(savedUsers))
                        .build())
                .build();
    }

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
                        .AccountName(getFullName(foundUser))
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
        return getFullName(foundUser);
    }

    @Override
    @Transactional
    public BankResponse creditAccount(CreditDebitRequest request) {
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

        Transactiondto transactionDto = Transactiondto.builder()
                .accountNumber(userToCredit.getAccountNumber())
                .transactionType("CREDIT")
                .amount(request.getAmount())
                .build();
        transactionService.saveTransaction(transactionDto);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREDITED_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_CREDITED_SUCCESS_MESSAGE)
                .accountName(getFullName(userToCredit))
                .AccountBalance(userToCredit.getAccountBalance())
                .AccountNumber(request.getAccountNumber())
                .build();
    }

    @Override
    @Transactional
    public BankResponse debitAccount(CreditDebitRequest request) {
        Boolean isAccountExist = usersRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        Users userToDebit = usersRepository.findByAccountNumber(request.getAccountNumber());

        if (userToDebit.getAccountBalance().compareTo(request.getAmount()) < 0) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        } else {
            userToDebit.setAccountBalance(userToDebit.getAccountBalance().subtract(request.getAmount()));
            usersRepository.save(userToDebit);

            Transactiondto transactionDto = Transactiondto.builder()
                    .accountNumber(userToDebit.getAccountNumber())
                    .transactionType("DEBIT")
                    .amount(request.getAmount())
                    .build();
            transactionService.saveTransaction(transactionDto);

            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_DEBITED_SUCCESSFULLY_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_DEBITED_SUCCESSFULLY_MESSAGE)
                    .accountInfo(AccountInfo.builder()
                            .AccountNumber(request.getAccountNumber())
                            .AccountName(getFullName(userToDebit))
                            .AccountBalance(userToDebit.getAccountBalance())
                            .build())
                    .build();
        }
    }

    @Override
    @Transactional
    public BankResponse transfer(TransferRequest request) {
        boolean isDesignationExist = usersRepository.existsByAccountNumber(request.getDesignationAccountNumber());
        if (!isDesignationExist) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        Users SourceAccountUser = usersRepository.findByAccountNumber(request.getSourceAccountNumber());

        if (request.getAmount().compareTo(SourceAccountUser.getAccountBalance()) > 0) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        SourceAccountUser.setAccountBalance(SourceAccountUser.getAccountBalance().subtract(request.getAmount()));
        usersRepository.save(SourceAccountUser);

        String SourceUserName = getFullName(SourceAccountUser);

        EmailDetails debitAlert = EmailDetails.builder()
                .subject("DEBIT ALERT")
                .recipient(SourceAccountUser.getEmail())
                .messageBody("Sum of " + request.getAmount() + " has been deducted from your account! Your current balance is: " + SourceAccountUser.getAccountBalance())
                .build();
        emailService.sentEmailAlerts(debitAlert);

        Users designationAccountUser = usersRepository.findByAccountNumber(request.getDesignationAccountNumber());
        designationAccountUser.setAccountBalance(designationAccountUser.getAccountBalance().add(request.getAmount()));
        usersRepository.save(designationAccountUser);

        EmailDetails creditAlert = EmailDetails.builder()
                .subject("CREDIT ALERT")
                .recipient(designationAccountUser.getEmail())
                .messageBody("Sum of " + request.getAmount() + " has been credited to your account from " + SourceUserName + "! Your current balance is: " + designationAccountUser.getAccountBalance())
                .build();
        emailService.sentEmailAlerts(creditAlert);

        // Save debit transaction for source account
        Transactiondto debitTransactionDto = Transactiondto.builder()
                .accountNumber(SourceAccountUser.getAccountNumber())
                .transactionType("DEBIT")
                .amount(request.getAmount())
                .build();
        transactionService.saveTransaction(debitTransactionDto);

        // Save credit transaction for designation account
        Transactiondto creditTransactionDto = Transactiondto.builder()
                .accountNumber(designationAccountUser.getAccountNumber())
                .transactionType("CREDIT")
                .amount(request.getAmount())
                .build();
        transactionService.saveTransaction(creditTransactionDto);

        return BankResponse.builder()
                .responseCode(AccountUtils.TRANSFER_SUCCESSFULLY_CODE)
                .responseMessage(AccountUtils.TRANSFER_SUCCESSFULLY_MESSAGE)
                .accountInfo(null)
                .build();
    }
}
