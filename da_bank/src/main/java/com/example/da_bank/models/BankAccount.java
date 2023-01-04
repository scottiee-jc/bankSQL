package com.example.da_bank.models;

import com.example.da_bank.Exceptions.InsufficientBalanceException;
import com.example.da_bank.Exceptions.NegativeAmountException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="bank_accounts")
public class BankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String name;
    String type;
    BigDecimal balance;
    @JsonIgnoreProperties
    @OneToMany
    private List<AccountUser> accountUser;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "statement_id", referencedColumnName = "id")
    private AccountStatement statement;

    @ManyToOne
    @JoinColumn(name = "money_id")
    private Money money;

    public Money getMoney() {
        return money;
    }

    public void setMoney(Money money) {
        this.money = money;
    }

    public BankAccount(Long id, String name, String type, BigDecimal balance, Money money) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.balance = balance;
        this.money = money;
    }

    public BankAccount(String name, String type, Money money) {
        this.accountUser = new ArrayList<>();
        this.name = name;
        this.type = type;
        this.money = money;
    }

    public BankAccount(){
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<AccountUser> getAccountUser() {
        return accountUser;
    }

    public void setAccountUser(List<AccountUser> accountUser) {
        this.accountUser = accountUser;
    }

    private void checkPositiveAmount(BigDecimal value) {
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeAmountException("Negative amount: " + value);
        }
    }

    private void checkSufficientFunds(BigDecimal value) {
        if (balance.compareTo(value) < 0) {
            throw new InsufficientBalanceException("Not enough funds to withdraw: " + value);
        }
    }

    public void depositMoney(BigDecimal value) {
        checkPositiveAmount(value);
        balance = balance.add(value);
    }

    public void withdrawMoney(BigDecimal value) {
        checkPositiveAmount(value);
        checkSufficientFunds(value);
        balance = balance.subtract(value);
    }

    public void transferMoneyTo(BigDecimal value, BankAccount destination) {
        withdrawMoney(value);
        destination.depositMoney(value);
    }

}
