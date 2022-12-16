package com.example.da_bank.controllers;

import com.example.da_bank.models.AccountUser;
import com.example.da_bank.models.BankAccount;
import com.example.da_bank.repositories.AccountUserRepository;
import com.example.da_bank.repositories.BankAccountRepository;
import com.example.da_bank.service.AccountUserService;
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
    AccountUserService accountUserService;

    public UserController(AccountUserRepository accountUserRepository, BankAccountRepository bankAccountRepository, AccountUserService accountUserService) {
        this.accountUserRepository = accountUserRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.accountUserService = accountUserService;
    }

    @GetMapping("/getAllUsers")
    public @ResponseBody String getAllUsers(){
        JSONArray jsonArray = new JSONArray();
        accountUserRepository.findAll().forEach((user) -> jsonArray.add(user.getFirstName() + " " + user.getLastName() + ", " + user.getCustomerNumber()));
        return jsonArray.toJSONString();
    }

    // String firstName, String lastName, int accountNumber, LocalDate dob, String username, String password
    @PostMapping("/createUserAccount")
    public @ResponseBody String createUserAccount(
            @RequestParam ("customer_number") int customerNumber,
            @RequestParam ("dob") String dob,
            @RequestParam ("first_name") String firstName,
            @RequestParam ("last_name") String lastName,
            @RequestParam ("password") String password,
            @RequestParam ("username") String username,
            @RequestParam ("email") String email,
            @RequestParam ("phone_number") String phone_number
    ){
        return accountUserService.createUserAccount(customerNumber, dob, firstName, lastName, password, username, email, phone_number);
    }

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
        return accountUserService.updateUserAccount(customerNumber, username, password, email, phone_number);
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


    @DeleteMapping("/deleteUserAccount/{id}")
    public void deleteUserAccount(@PathVariable Long id){
        accountUserRepository.deleteById(id);
    }

}
