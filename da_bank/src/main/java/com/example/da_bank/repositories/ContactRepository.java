package com.example.da_bank.repositories;

import com.example.da_bank.models.AccountUser;
import com.example.da_bank.models.Contacts;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ContactRepository extends CrudRepository<Contacts, Long> {

    @Query(value = "SELECT * FROM contacts WHERE username_a = ?", nativeQuery = true)
    Contacts findContactA(String username);

    @Query(value = "SELECT * FROM contacts WHERE username_b = ?", nativeQuery = true)
    Contacts findContactB(String username);

    @Query(value = "SELECT * FROM contacts WHERE username_a =?1 AND username_b = ?2", nativeQuery = true)
    Contacts findContactPair(String username_a, String username_b);
}
