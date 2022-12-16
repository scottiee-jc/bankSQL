package com.example.da_bank.repositories;

import com.example.da_bank.models.BankAccount;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface BankAccountRepository extends CrudRepository<BankAccount, Long> {

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO bank_accounts (name, type) " + "VALUES (?1,?2)", nativeQuery = true)
    void addAccount(String name, String type);

    @Query(value = "SELECT * FROM bank_accounts WHERE id = ?", nativeQuery = true)
    BankAccount findAccountById(Long id);

    @Query(value = "SELECT * FROM bank_accounts WHERE name = ?", nativeQuery = true)
    BankAccount findAccountTypeByName(String name);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM bank_accounts WHERE name = ?", nativeQuery = true)
    void deleteBankAccountType(String name);
}
