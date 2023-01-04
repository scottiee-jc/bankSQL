package com.example.da_bank.controllers;

import com.example.da_bank.models.BankAccount;
import com.example.da_bank.repositories.BankAccountRepository;
import com.example.da_bank.service.BankAccountService;
import org.json.simple.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/accounts")
public class AccountsController {

    @Autowired
    BankAccountRepository bankAccountRepository;

    @Autowired
    BankAccountService bankAccountService;

    public AccountsController(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    @PostMapping
    @RolesAllowed("ROLE_EDITOR")
    protected ResponseEntity<BankAccount> createBA(@RequestBody @Valid BankAccount bankAccount) {
        BankAccount savedBA = bankAccountRepository.save(bankAccount);
        URI accountURI = URI.create("/accounts/" + savedBA.getId());
        return ResponseEntity.created(accountURI).body(savedBA);
    }

    @GetMapping("/getAllAccounts")
    @RolesAllowed({"ROLE_CUSTOMER", "ROLE_EDITOR"})
    public @ResponseBody String getAll(){
        JSONArray jsonArray = new JSONArray();
        bankAccountRepository.findAll().forEach((account) -> jsonArray.add(account.getName() + " is a " + account.getType() + " type."));
        return jsonArray.toJSONString();
    }

    @GetMapping("/getAccountById/{id}")
    @RolesAllowed("ROLE_EDITOR")
    public @ResponseBody BankAccount getAccountById(@PathVariable Long id) throws NullPointerException {
        BankAccount exists = bankAccountRepository.findAccountById(id);
        if (exists != null){
            return exists;
        } else {
            throw new NullPointerException("Error: Account doesn't exist. Please enter another ID.");
        }
    }

    @GetMapping("/findAccountTypeByName/{name}")
    @RolesAllowed({"ROLE_CUSTOMER", "ROLE_EDITOR"})
    public @ResponseBody BankAccount findAccountTypeByName(@PathVariable String name) throws NullPointerException {
        BankAccount exists = bankAccountRepository.findAccountTypeByName(name);
        if (exists != null){
            return exists;
        } else {
            throw new NullPointerException("Error: Account doesn't exist. Please check the name of the account you are searching for.");
        }
    }

    @PostMapping("/addAccount")
    @RolesAllowed({"ROLE_CUSTOMER", "ROLE_EDITOR"})
    public @ResponseBody String addAccount(
            @RequestParam("name") String name,
            @RequestParam("type") String type
    ){
        return bankAccountService.addAccount(name, type);
    }

    @DeleteMapping("/deleteBankAccountType/{name}")
    @RolesAllowed({"ROLE_CUSTOMER", "ROLE_EDITOR"})
    public String deleteBankAccountType(@PathVariable String name){
        return bankAccountService.deleteAccountType(name);
    }

}
