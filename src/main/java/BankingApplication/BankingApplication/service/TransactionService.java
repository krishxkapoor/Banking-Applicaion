package BankingApplication.BankingApplication.service;

import BankingApplication.BankingApplication.dto.Transactiondto;

public interface TransactionService {
    void saveTransaction(Transactiondto transactionDto);
}
