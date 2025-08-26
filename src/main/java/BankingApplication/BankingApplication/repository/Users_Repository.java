
package BankingApplication.BankingApplication.repository;

import BankingApplication.BankingApplication.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Users_Repository extends JpaRepository<Users, Long>{
    Boolean existsByEmail(String email);
//    Boolean existsByAccountNumber
    Boolean existsByAccountNumber (String accountNumber);
    Users findByAccountNumber(String accountNumber);
}
