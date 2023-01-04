package com.example.da_bank.service.AccountUserService;

import com.example.da_bank.models.AccountUser;
import com.example.da_bank.models.Role;

import java.util.List;

public interface AccountUserService {

    AccountUser saveUser(AccountUser user);
    Role saveRole(Role role);
    void addRoleToUser(String username, String roleName);
    AccountUser getUser(String username);
    List<AccountUser> getUsers();
    String createUserAccount(int customerNumber, String dob, String firstName, String lastName, String password, String username, String email, String phone_number);
}
