package BankingApplication.BankingApplication.utils;

import java.time.Year;

public class AccountUtils {
    public static final String ACCOUNT_EXISTS_CODE = "001";
    public static final String ACCOUNT_EXISTS_MESSAGE = "This User Already Has Account";
    public static final String ACCOUNT_CREATION_CODE = "002";
    public static final String ACCOUNT_CREATION_MESSAGE = "Account Successfully Created";
    public static final String ACCOUNT_NOT_EXISTS_CODE = "003";
    public static final String ACCOUNT_NOT_EXISTS_MESSAGE = "User with the provided Account Number Does Not Exist";
    public static final String ACCOUNT_FOUND_CODE = "004";
    public static final String ACCOUNT_FOUND_MESSAGE = "User Account Found";
    public static final String ACCOUNT_CREDITED_SUCCESS = "005";
    public static final String ACCOUNT_CREDITED_SUCCESS_MESSAGE = "Amount Credited Successfully";
    public static final String INSUFFICIENT_BALANCE_CODE = "006";
    public static final String INSUFFICIENT_BALANCE_MESSAGE = "Insufficient Balance";
    public static final String ACCOUNT_DEBITED_SUCCESSFULLY_CODE = "007";
    public static final String ACCOUNT_DEBITED_SUCCESSFULLY_MESSAGE = "Account has been successfully debited";
    /*
    2025+RandomSixDigits concat
    2025237442
    */
    Year currentYear = Year.now();
    int min=100000;
    int max=999999;
    // random number btwn min and max
    int randomNumber = (int) Math.floor(Math.random() * (max-min +1)+min);
    //convert current nd randomNumber to string and then concate
    String year=String.valueOf(currentYear);
    String RandomNumber = String.valueOf(randomNumber);
    StringBuilder accountNumber=new StringBuilder();
    public static String genrateAccountNumber(){

    Year currentYear = Year.now();
    
    int min=100000;
    int max=999999;
    
    // random number btwn min and max
    int randomNumber = (int) Math.floor(Math.random() * (max-min +1)+min);
    //convert current nd randomNumber to string and then concate
    
    String year=String.valueOf(currentYear);
    
    String RandomNumber = String.valueOf(randomNumber);
    
    StringBuilder accountNumber=new StringBuilder();
    
    return accountNumber.append(year).append(RandomNumber).toString();
    }
}
