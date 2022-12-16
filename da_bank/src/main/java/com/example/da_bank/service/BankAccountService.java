package com.example.da_bank.service;

import com.example.da_bank.models.BankAccount;
import com.example.da_bank.repositories.BankAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BankAccountService {

    @Autowired
    BankAccountRepository bankAccountRepository;

    public BankAccountService(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    public String addAccount(String name,
                       String type){
        BankAccount existingAccountType = bankAccountRepository.findAccountTypeByName(name);

        if(existingAccountType != null){
            throw new RuntimeException("Bank Account name already exists.");
        } else {
            bankAccountRepository.addAccount(name, type);
        }
        return "Bank Account Product Added";
    }

    public String deleteAccountType (String name){
        BankAccount existingAccountType = bankAccountRepository.findAccountTypeByName(name);

        if(existingAccountType == null){
            throw new RuntimeException("Bank Account type: " + "'" + name + "'" + " does not exist. Check the details and try again.");
        } else {
            bankAccountRepository.deleteBankAccountType(name);
            return "Bank Account type: " + "'" + name + "'" + " deleted";
        }
    }
}
