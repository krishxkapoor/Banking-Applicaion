package BankingApplication.BankingApplication.service;

import BankingApplication.BankingApplication.dto.EmailDetails;

public interface EmailService {
    void sentEmailAlerts(EmailDetails emailDetails);
     
}
