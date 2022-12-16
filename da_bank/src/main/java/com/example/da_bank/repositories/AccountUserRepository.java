package com.example.da_bank.repositories;

import com.example.da_bank.models.AccountUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface AccountUserRepository extends JpaRepository<AccountUser, Long> {
    @Query(value = "SELECT * FROM account_holders WHERE id = ?", nativeQuery = true)
    Optional<AccountUser> getUsername(String username);
    @Query(value = "SELECT * FROM account_holders WHERE id = ?", nativeQuery = true)
    AccountUser findUserById(Long id);
    @Transactional
    @Modifying
    @Query(value = "INSERT INTO account_holders (customer_number,dob,first_name,last_name,password,username,email,phone_number) " + "VALUES(?1,?2,?3,?4,?5,?6,?7,?8)", nativeQuery = true)
    void createUserAccount(int customerNumber, String dob, String firstname, String lastname, String password, String username, String email, String phone_number);

    @Query(value = "SELECT * FROM account_holders WHERE customer_number = ?", nativeQuery = true)
    AccountUser findUserByCustomerNumber(int customerNumber);

    @Query(value = "SELECT * FROM account_holders WHERE username = ?", nativeQuery = true)
    AccountUser findUserByUsername(String username);

    @Transactional
    @Modifying
    @Query(value = "UPDATE account_holders SET "
            + "password = ?1, "
            + "username = ?2, "
            + "email = ?3, "
            + "phone_number = ?4 "
            + "WHERE customer_number = ?1", nativeQuery = true)
    void updateUserAccount(String password, String username, String email, String phone_number, int customerNumber);


//    @Query(value = "SELECT bank_account_id FROM account_holders WHERE customer_number = ?", nativeQuery = true)
//    List<BankAccount> findUserBankAccounts(int customerNumber);
}
