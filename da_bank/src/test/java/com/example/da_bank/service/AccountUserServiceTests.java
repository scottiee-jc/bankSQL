package com.example.da_bank.service;

import com.example.da_bank.models.AccountUser;
import com.example.da_bank.models.Role;
import com.example.da_bank.repositories.AccountUserRepository;
import com.example.da_bank.service.AccountUserService.AccountUserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.Rollback;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(value = false)
public class AccountUserServiceTests {

    @Autowired
    AccountUserServiceImpl accountUserServiceImpl;

    @Autowired
    AccountUserRepository accountUserRepository;

    @Test
    public void testCreateUser(){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String password = passwordEncoder.encode("mowrk");

        AccountUser newUser = new AccountUser("mowrktheshowrk", password, 3335678);
        AccountUser savedUser = accountUserRepository.save(newUser);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isGreaterThan(0);

        System.out.println(savedUser.getUsername());
    }

    @Test
    public void emailTest() {
        String emailAddress = "username@domain.com";
        String regexPattern = "^(.+)@(\\S+)$";
        assertTrue(accountUserServiceImpl.patternMatches(emailAddress, regexPattern));

//        assertTrue(EmailValidation.patternMatches(emailAddress, regexPattern));
    }

    @Test
    public void phoneNumberTest(){
        String phone_number = "7762572107";
        String regexPhone = "^\\d{10}$";
        assertTrue(accountUserServiceImpl.patternMatches(phone_number, regexPhone));
    }

    @Test
    public void testAssignRoleToUser(){
        Long userId = 4L;
        Long roleId = 3L;
        AccountUser user = accountUserRepository.findById(userId).get();
        user.addRole(new Role(roleId));

        AccountUser updateUser = accountUserRepository.save(user);
        assertThat(updateUser.getRoles()).hasSize(1);
    }

    @Test
    public void testAssignRoleToUser2(){
        AccountUser user = accountUserRepository.findUserById(2L);
        user.addRole(new Role(1L));
        user.addRole(new Role(2L));

        AccountUser updateUser = accountUserRepository.save(user);
        assertThat(updateUser.getRoles()).hasSize(2);
    }
}
