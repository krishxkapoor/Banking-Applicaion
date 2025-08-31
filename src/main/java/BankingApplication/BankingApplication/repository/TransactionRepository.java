package BankingApplication.BankingApplication.repository;

import BankingApplication.BankingApplication.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, String>{
    
}
