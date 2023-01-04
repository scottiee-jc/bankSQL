package com.example.da_bank.controllers;

import com.example.da_bank.models.AccountUser;
import com.example.da_bank.models.BankAccount;
import com.example.da_bank.repositories.AccountUserRepository;
import com.example.da_bank.repositories.BankAccountRepository;
import com.example.da_bank.service.AccountUserService.AccountUserServiceImpl;
import org.json.simple.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("localhost:3000")
public class UserController {

    @Autowired
    AccountUserRepository accountUserRepository;

    @Autowired
    BankAccountRepository bankAccountRepository;

    @Autowired
    AccountUserServiceImpl accountUserServiceImpl;

    public UserController(AccountUserRepository accountUserRepository, BankAccountRepository bankAccountRepository, AccountUserServiceImpl accountUserServiceImpl) {
        this.accountUserRepository = accountUserRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.accountUserServiceImpl = accountUserServiceImpl;
    }

    @GetMapping("/getAllUsers")
    public @ResponseBody String getAllUsers(){
        JSONArray jsonArray = new JSONArray();
        accountUserRepository.findAll().forEach((user) -> jsonArray.add(user.getFirstName() + " " + user.getLastName() + ", " + user.getCustomerNumber()));
        return jsonArray.toJSONString();
    }

    // String firstName, String lastName, int accountNumber, LocalDate dob, String username, String password


    @GetMapping("/getUserById/{id}")
    public @ResponseBody AccountUser getUserById(@PathVariable Long id){
        return accountUserRepository.findUserById(id);
    }


    @PutMapping("/updateUserAccount/{customerNumber}")
    public @ResponseBody String updateUserAccount(
            @PathVariable int customerNumber,
            @RequestParam (required = false, name = "password") String password,
            @RequestParam (required = false, name = "username") String username,
            @RequestParam (required = false, name = "email") String email,
            @RequestParam (required = false, name = "phone_number") String phone_number
            ){
        return accountUserServiceImpl.updateUserAccount(customerNumber, username, password, email, phone_number);
    }

    @PostMapping("/addBankAccount")
    public @ResponseBody String addBankAccount(
            @RequestParam (required = true, name = "input") String input,
            @RequestParam(required = true, name = "customerNumber") int customerNumber
    ){
        BankAccount bankAccountToAdd = bankAccountRepository.findAccountTypeByName(input);
        AccountUser accountUser = accountUserRepository.findUserByCustomerNumber(customerNumber);
        if (accountUser.getBankAccount().contains(bankAccountToAdd)){
            throw new RuntimeException("You already have an account of type " + bankAccountToAdd);
        } else {
            accountUser.getBankAccount().add(bankAccountToAdd);
            accountUserRepository.save(accountUser);
            JSONArray jsonArray = new JSONArray();
            accountUser.getBankAccount().forEach((account) -> jsonArray.add("Name of account: " + account.getName() + ", Type: " + account.getType() + ", Product ID: " + account.getId()));
            return "Bank account added successfully. Here is an overview of your accounts: " + jsonArray.toJSONString();
        }

    }


}
