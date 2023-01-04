package com.example.da_bank.service.AccountUserService;

import com.example.da_bank.models.AccountUser;
import com.example.da_bank.models.BankAccount;
import com.example.da_bank.models.Contacts;
import com.example.da_bank.models.Role;
import com.example.da_bank.repositories.AccountUserRepository;
import com.example.da_bank.repositories.BankAccountRepository;
import com.example.da_bank.repositories.ContactRepository;
import com.example.da_bank.repositories.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service @Slf4j
public class AccountUserServiceImpl implements AccountUserService, UserDetailsService {

    @Autowired
    ContactRepository contactRepository;
    @Autowired
    AccountUserRepository accountUserRepository;
    @Autowired
    BankAccountRepository bankAccountRepository;
    @Autowired
    RoleRepository roleRepository;

    public AccountUserServiceImpl(AccountUserRepository accountUserRepository, BankAccountRepository bankAccountRepository) {
        this.accountUserRepository = accountUserRepository;
        this.bankAccountRepository = bankAccountRepository;
    }


    @Override
    public AccountUser saveUser(AccountUser user) {
        log.info("Saving new user {} to the database", user.getFirstName());
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return accountUserRepository.save(user);
    }

    @Override
    public Role saveRole(Role role) {
        return null;
    }

    @Override
    public void addRoleToUser(String username, String roleName) {
        AccountUser user = accountUserRepository.findUserByUsername(username);
        Role role = roleRepository.findByRoleName(roleName);
        log.info("Adding role {} to user {} ", roleName, username);
        user.getRoles().add(role);
    }

    @Override
    public AccountUser getUser(String username) {
        log.info("Fetching user {} ", username);
        return accountUserRepository.findUserByUsername(username);
    }

    @Override
    public List<AccountUser> getUsers() {
        log.info("Fetching all users");
        return accountUserRepository.findAll();
    }

    @Override
    public String createUserAccount (int customerNumber, String dob, String firstname, String lastname, String password, String username, String email, String phone_number) throws RuntimeException {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = bCryptPasswordEncoder.encode(password);
        AccountUser existingUserCN = accountUserRepository.findUserByCustomerNumber(customerNumber);
        AccountUser existingUserUN = accountUserRepository.findUserByUsername(username);
        String regexPattern = new String("^(.+)@(\\S+)$");
        String regexPhone = new String("^\\d{10}$");

        if (existingUserCN != null || existingUserUN != null){
            return "Error: Customer Account already exists.";
        } else if ((username.length() >= 6) && (password.length() >= 8) && (patternMatches(email, regexPattern)) && (phoneNumberis11(phone_number, regexPhone))) {
            throw new RuntimeException("Those details aren't quite right. Please ensure that your username and password are greater than 8 and 6 characters respectively, your email contains an '@' and your phone number contains 10 numbers (don't include the initial 0 or country code)");
        } else {
            accountUserRepository.createUserAccount(customerNumber, dob, firstname, lastname, encodedPassword, username, email, phone_number);
            return "Account created. Welcome to Da Bank";
        }
    }

    public boolean patternMatches(String emailAddress, String regexPattern){
        return Pattern.compile(regexPattern).matcher(emailAddress).matches();
    }

    public boolean phoneNumberis11(String phone_number, String regexPattern){
        return Pattern.compile(regexPattern).matcher(phone_number).matches();
    }

    public String updateUserAccount (int customerNumber, String username, String password, String email, String phone_number) throws NullPointerException {

        AccountUser existingUser = accountUserRepository.findUserByCustomerNumber(customerNumber);
        String regexPattern = new String("^(.+)@(\\S+)$");
        String regexPhone = new String("^\\d{10}$");
        AccountUser otherUser = accountUserRepository.findUserByUsername(username);
        Contacts contactA = contactRepository.findContactA(existingUser.getUsername());
        Contacts contactB = contactRepository.findContactB(existingUser.getUsername());

        if (existingUser != null){

            // BLOCK ONE ---- ALL PARAMETERS ARE BEING UPDATED

            if (username != null && password != null && email != null && phone_number != null) {

                if (username.equals(existingUser.getUsername())){
                    throw new RuntimeException("You have entered the same username that we have on our records. Please update with a new one, or return to your account area");
                } else if (password.equals(existingUser.getPassword())){
                    throw new RuntimeException("You have entered the same password that we have on our records. Please update with a new one, or return to your account area");
                } else if (email.equals(existingUser.getEmail())) {
                    throw new RuntimeException("You have entered the same email that we have on our records. Please update with a new one, or return to your account area");
                } else if (phone_number.equals(existingUser.getPhone_number())) {
                    throw new RuntimeException("You have entered the same phone number that we have on our records. Please update with a new one, or return to your account area");
                } else if ((username.length() >= 6 && otherUser == null) && (password.length() >= 8) && (patternMatches(email, regexPattern)) && (phoneNumberis11(phone_number, regexPhone))) {
                    if (contactA != null) {
                        contactA.setUsername_a(username);
                        contactRepository.save(contactA);
                    } else if (contactB != null) {
                        contactB.setUsername_b(username);
                        contactRepository.save(contactB);
                    } else {
                                return null;
                    }
                    existingUser.setUsername(username);
                    existingUser.setPassword(password);
                    existingUser.setEmail(email);
                    existingUser.setPhone_number(phone_number);
                    accountUserRepository.save(existingUser);
                    return "Account details updated. username set to " + existingUser.getUsername() + " and password set to " + existingUser.getPassword() + " and email set to " + existingUser.getEmail() + " and phone number set to " + existingUser.getPhone_number();
                } else {
                    if (username.equals(otherUser.getUsername())) {
                        throw new RuntimeException("Username already taken. Please try another.");
                    } else {
                        throw new RuntimeException("Those details aren't quite right. Please check that your username is greater than 6 characters, your password is greater than 8, your email contains an @ and/or your phone number is 11 digits - don't include your country code / initial 0.");
                    }
                }

                // BLOCK TWO ------- USERNAME, PASSWORD AND EMAIL PARAMS FILLED OUT

            } else if (username != null && password != null && email != null){

                if (username.equals(existingUser.getUsername())){
                    throw new RuntimeException("You have entered the same username that we have on our records. Please update with a new one, or return to your account area");
                } else if (password.equals(existingUser.getPassword())){
                    throw new RuntimeException("You have entered the same password that we have on our records. Please update with a new one, or return to your account area");
                } else if (email.equals(existingUser.getEmail())) {
                    throw new RuntimeException("You have entered the same email that we have on our records. Please update with a new one, or return to your account area");
                } else if ((username.length() >= 6 && otherUser == null) && (password.length() >= 8) && (patternMatches(email, regexPattern))){
                    if (contactA != null) {
                        contactA.setUsername_a(username);
                        contactRepository.save(contactA);
                    } else if (contactB != null) {
                        contactB.setUsername_b(username);
                        contactRepository.save(contactB);
                    } else {
                        return null;
                    }
                    existingUser.setEmail(email);
                    existingUser.setUsername(username);
                    existingUser.setPassword(password);
                    accountUserRepository.save(existingUser);
                    return "Account details updated: username set to " + existingUser.getUsername() + " and password set to " + existingUser.getPassword() + " and email set to " + existingUser.getEmail();
                } else {
                    if (username.equals(otherUser.getUsername())) {
                        throw new RuntimeException("Username already taken. Please try another.");
                    } else {
                        throw new RuntimeException("Those details aren't quite right. Please check that your username is greater than 6 characters, your password is greater than 8, your email contains an @ and/or your phone number is 11 digits - don't include your country code / initial 0.");
                    }
                }

                // BLOCK 3 -------- USERNAME, PASSWORD AND PHONE NUMBER ARE FILLED OUT

            } else if (username != null && password != null && phone_number != null){

                if (username.equals(existingUser.getUsername())){
                    throw new RuntimeException("You have entered the same username that we have on our records. Please update with a new one, or return to your account area");
                } else if (password.equals(existingUser.getPassword())){
                    throw new RuntimeException("You have entered the same password that we have on our records. Please update with a new one, or return to your account area");
                } else if (phone_number.equals(existingUser.getPhone_number())) {
                    throw new RuntimeException("You have entered the same phone number that we have on our records. Please update with a new one, or return to your account area");
                } else if ((username.length() >= 6 && otherUser == null) && (password.length() >= 8) && (phoneNumberis11(phone_number, regexPhone))){
                    if (contactA != null) {
                        contactA.setUsername_a(username);
                        contactRepository.save(contactA);
                    } else if (contactB != null) {
                        contactB.setUsername_b(username);
                        contactRepository.save(contactB);
                    } else {
                        return null;
                    }
                    existingUser.setPhone_number(phone_number);
                    existingUser.setUsername(username);
                    existingUser.setPassword(password);
                    accountUserRepository.save(existingUser);
                    return "Account details updated: username set to " + existingUser.getUsername() + " and password set to " + existingUser.getPassword() + " and phone number set to " + existingUser.getPhone_number();
                } else {
                    if (username.equals(otherUser.getUsername())) {
                        throw new RuntimeException("Username already taken. Please try another.");
                    } else {
                        throw new RuntimeException("Those details aren't quite right. Please check that your username is greater than 6 characters, your password is greater than 8, your email contains an @ and/or your phone number is 11 digits - don't include your country code / initial 0.");
                    }
                }

                // BLOCK 4 -------- USERNAME, EMAIL AND PHONE NUMBER ARE FILLED OUT

            } else if (username != null && email != null && phone_number != null){

                if (username.equals(existingUser.getUsername())){
                    throw new RuntimeException("You have entered the same username that we have on our records. Please update with a new one, or return to your account area");
                } else if (email.equals(existingUser.getEmail())) {
                    throw new RuntimeException("You have entered the same email that we have on our records. Please update with a new one, or return to your account area");
                } else if (phone_number.equals(existingUser.getPhone_number())) {
                    throw new RuntimeException("You have entered the same phone number that we have on our records. Please update with a new one, or return to your account area");
                } else if ((username.length() >= 6 && otherUser == null) && (patternMatches(email, regexPattern)) && (phoneNumberis11(phone_number, regexPhone))){
                    if (contactA != null) {
                        contactA.setUsername_a(username);
                        contactRepository.save(contactA);
                    } else if (contactB != null) {
                        contactB.setUsername_b(username);
                        contactRepository.save(contactB);
                    } else {
                        return null;
                    }
                    existingUser.setEmail(email);
                    existingUser.setUsername(username);
                    existingUser.setPhone_number(phone_number);
                    accountUserRepository.save(existingUser);
                    return "Account details updated: username set to " + existingUser.getUsername() + " and phone number set to " + existingUser.getPhone_number() + " and email set to " + existingUser.getEmail();
                } else {
                    if (username.equals(otherUser.getUsername())) {
                        throw new RuntimeException("Username already taken. Please try another.");
                    } else {
                        throw new RuntimeException("Those details aren't quite right. Please check that your username is greater than 6 characters, your password is greater than 8, your email contains an @ and/or your phone number is 11 digits - don't include your country code / initial 0.");
                    }
                }

                // BLOCK 5 --------- PASSWORD, EMAIL AND PHONE NUMBER ARE FILLED OUT

            } else if (password != null && email != null && phone_number != null){

                if (password.equals(existingUser.getPassword())){
                    throw new RuntimeException("You have entered the same password that we have on our records. Please update with a new one, or return to your account area");
                } else if (email.equals(existingUser.getEmail())) {
                    throw new RuntimeException("You have entered the same email that we have on our records. Please update with a new one, or return to your account area");
                } else if (phone_number.equals(existingUser.getPhone_number())) {
                    throw new RuntimeException("You have entered the same phone number that we have on our records. Please update with a new one, or return to your account area");
                } else if ((password.length() >= 8) && (patternMatches(email, regexPattern)) && (phoneNumberis11(phone_number, regexPhone))){
                    existingUser.setPassword(password);
                    existingUser.setEmail(email);
                    existingUser.setPhone_number(phone_number);
                    accountUserRepository.save(existingUser);
                    return "Account details updated: password set to " + existingUser.getPassword() + " and phone number set to " + existingUser.getPhone_number() + " and email set to " + existingUser.getEmail();
                } else {
                    throw new RuntimeException("Those details aren't quite right. Please check that your password is greater than 8 characters, your email contains an @ and/or your phone number is 11 digits - don't include your country code / initial 0.");
                }

                // BLOCK 6 --------- USERNAME AND EMAIL ARE FILLED OUT

            } else if (username != null && email != null){

                if(username.equals(existingUser.getUsername())){
                    throw new RuntimeException("You have entered the same username that we have on our records. Please update with a new one, or return to your account area");
                } else if (email.equals(existingUser.getEmail())) {
                    throw new RuntimeException("You have entered the same email that we have on our records. Please update with a new one, or return to your account area");
                } else if ((username.length() >= 6 && otherUser == null) && (patternMatches(email, regexPattern))){
                    if (contactA != null) {
                        contactA.setUsername_a(username);
                        contactRepository.save(contactA);
                    } else if (contactB != null) {
                        contactB.setUsername_b(username);
                        contactRepository.save(contactB);
                    } else {
                        return null;
                    }
                    existingUser.setUsername(username);
                    existingUser.setEmail(email);
                    accountUserRepository.save(existingUser);
                    return "Account details updated: username set to " + existingUser.getUsername() + " and email set to " + existingUser.getEmail();
                } else {
                    if (username.equals(otherUser.getUsername())) {
                        throw new RuntimeException("Username already taken. Please try another.");
                    } else {
                        throw new RuntimeException("Those details aren't quite right. Please check that your username is greater than 6 characters and your email contains an @");
                    }
                }

                // BLOCK 7 -------- USERNAME AND PASSWORD ARE FILLED OUT

            } else if (username != null && password != null){

                if (username.equals(existingUser.getUsername())){
                    throw new RuntimeException("You have entered the same username that we have on our records. Please update with a new one, or return to your account area");
                } else if (password.equals(existingUser.getPassword())) {
                    throw new RuntimeException("You have entered the same password that we have on our records. Please update with a new one, or return to your account area");
                } else if ((username.length() >= 6 && otherUser == null) && (password.length() >= 8)){
                    if (contactA != null) {
                        contactA.setUsername_a(username);
                        contactRepository.save(contactA);
                    } else if (contactB != null) {
                        contactB.setUsername_b(username);
                        contactRepository.save(contactB);
                    } else {
                        return null;
                    }
                    existingUser.setUsername(username);
                    existingUser.setPassword(password);
                    accountUserRepository.save(existingUser);
                    return "Account details updated: username set to " + existingUser.getUsername() + " and password set to " + existingUser.getPassword();
                } else {
                    if (username.equals(otherUser.getUsername())) {
                        throw new RuntimeException("Username already taken. Please try another.");
                    } else {
                        throw new RuntimeException("Those details aren't quite right. Please check that your username is greater than 6 characters and your password is greater than 8");
                    }
                }

                // BLOCK 8 --------- USERNAME AND PHONE NUMBER ARE FILLED OUT

            } else if (username != null && phone_number != null){

                if (username.equals(existingUser.getUsername())){
                    throw new RuntimeException("You have entered the same username that we have on our records. Please update with a new one, or return to your account area");
                } else if (phone_number.equals(existingUser.getPhone_number())) {
                    throw new RuntimeException("You have entered the same phone number that we have on our records. Please update with a new one, or return to your account area");
                } else if ((username.length() >= 6 && otherUser == null) && (phoneNumberis11(phone_number, regexPhone))){
                    if (contactA != null) {
                        contactA.setUsername_a(username);
                        contactRepository.save(contactA);
                    } else if (contactB != null) {
                        contactB.setUsername_b(username);
                        contactRepository.save(contactB);
                    } else {
                        return null;
                    }
                    existingUser.setUsername(username);
                    existingUser.setPhone_number(phone_number);
                    accountUserRepository.save(existingUser);
                    return "Account details updated: username set to " + existingUser.getUsername() + " and phone number set to " + existingUser.getPhone_number();
                } else {
                    if (username.equals(otherUser.getUsername())) {
                        throw new RuntimeException("Username already taken. Please try another.");
                    } else {
                        throw new RuntimeException("Those details aren't quite right. Please check that your username is greater than 6 characters and/or your phone number is 11 digits - don't include your country code / initial 0.");
                    }
                }

                // BLOCK 9 ---------- PASSWORD AND PHONE NUMBER ARE FILLED OUT

            } else if (password != null && phone_number != null){

                if (password.equals(existingUser.getPassword())){
                    throw new RuntimeException("You have entered the same password that we have on our records. Please update with a new one, or return to your account area");
                } else if (phone_number.equals(existingUser.getPhone_number())) {
                    throw new RuntimeException("You have entered the same phone number that we have on our records. Please update with a new one, or return to your account area");
                } else if ((password.length() >= 8) && (phoneNumberis11(phone_number, regexPhone))){
                    existingUser.setPhone_number(phone_number);
                    existingUser.setPassword(password);
                    accountUserRepository.save(existingUser);
                    return "Account details updated: phone number set to " + existingUser.getPhone_number() + " and password set to " + existingUser.getPassword();
                } else {
                    throw new RuntimeException("Those details aren't quite right. Please check that your password is greater than 8 characters and/or your phone number is 11 digits - don't include your country code / initial 0.");
                }

            // BLOCK 10 ------ PASSWORD AND EMAIL ARE FILLED OUT

            } else if (password != null && email != null){

                if (password.equals(existingUser.getPassword())){
                    throw new RuntimeException("You have entered the same password that we have on our records. Please update with a new one, or return to your account area");
                } else if (email.equals(existingUser.getEmail())) {
                    throw new RuntimeException("You have entered the same email that we have on our records. Please update with a new one, or return to your account area");
                }
                else if ((password.length() >= 8) && (patternMatches(email, regexPattern))){
                    existingUser.setPassword(password);
                    existingUser.setEmail(email);
                    accountUserRepository.save(existingUser);
                    return "Account details updated: email set to " + existingUser.getEmail() + " and password set to " + existingUser.getPassword();
                } else {
                    throw new RuntimeException("Those details aren't quite right. Please check that your password is greater than 8 characters and your email contains an @");
                }

                // BLOCK 11 ------ EMAIL AND PHONE NUMBER ARE FILLED OUT

            } else if (email != null && phone_number != null){

                if (phone_number.equals(existingUser.getPhone_number())){
                    throw new RuntimeException("You have entered the same number that we have on our records. Please update with a new one, or return to your account area");
                } else if (email.equals(existingUser.getEmail())){
                    throw new RuntimeException("You have entered the same email that we have on our records. Please update with a new one, or return to your account area");
                } else if (patternMatches(email, regexPattern) && phoneNumberis11(phone_number, regexPhone)){
                    existingUser.setEmail(email);
                    existingUser.setPhone_number(phone_number);
                    accountUserRepository.save(existingUser);
                    return "Account details updated: email set to " + existingUser.getEmail() + " and phone number set to " + existingUser.getPhone_number();
                } else {
                    throw new RuntimeException("Those details aren't quite right. Please check that your email contains an @ and/or your phone number is 11 digits - don't include your country code / initial 0.");                }

            // BLOCK 12 -------- ONLY PASSWORD IS FILLED OUT

            } else if (password != null){

                if (password.equals(existingUser.getPassword())){
                    throw new RuntimeException("You have entered the same password that we have on our records. Please update with a new one, or return to your account area");
                } else if (password.length() >= 8){
                    existingUser.setPassword(password);
                    accountUserRepository.save(existingUser);
                    return "Account details updated: password set to " + existingUser.getPassword();
                } else {
                        throw new RuntimeException("Those details aren't quite right. Please check that your password is greater than 8 characters");
                }

                // BLOCK 13 -------- ONLY USERNAME IS FILLED OUT

            } else if (username != null) {

                if (username.equals(existingUser.getUsername())){
                    throw new RuntimeException("You have entered the same username that we have on our records. Please update with a new one, or return to your account area");
                } else if (username.length() >= 6 && otherUser == null ){
                    if (contactA != null) {
                        contactA.setUsername_a(username);
                        contactRepository.save(contactA);
                    } else if (contactB != null) {
                        contactB.setUsername_b(username);
                        contactRepository.save(contactB);
                    } else {
                        return null;
                    }
                    existingUser.setUsername(username);
                    accountUserRepository.save(existingUser);
                    return "Account details updated: username set to " + existingUser.getUsername();
                } else {
                    if (username.equals(otherUser.getUsername())) {
                        throw new RuntimeException("Username already taken. Please try another.");
                    } else {
                        throw new RuntimeException("Those details aren't quite right. Please check that your username is greater than 6 characters");
                    }
                }

                // BLOCK 14 -------- ONLY EMAIL IS FILLED OUT

            } else if (email != null) {

                if (email.equals(existingUser.getEmail())){
                    throw new RuntimeException("You have entered the same email that we have on our records. Please update with a new one, or return to your account area");
                } else if (patternMatches(email, regexPattern)){
                    existingUser.setEmail(email);
                    accountUserRepository.save(existingUser);
                    return "Account details updated: email set to " + existingUser.getEmail();
                } else {
                    throw new RuntimeException("Those details aren't quite right. Please check that your email contains an @.");
                }

                // BLOCK 14 -------- ONLY PHONE NUMBER IS FILLED OUT

            } else if (phone_number != null) {

                if (phone_number.equals(existingUser.getPhone_number())){
                    throw new RuntimeException("You have entered the same number that we have on our records. Please update with a new one, or return to your account area");
                } else if (phoneNumberis11(phone_number, regexPhone)){
                    existingUser.setPhone_number(phone_number);
                    accountUserRepository.save(existingUser);
                    return "Account details updated: phone number set to " + existingUser.getPhone_number();
                } else {
                    throw new RuntimeException("Those details aren't quite right. Please check that your phone number contains 10 digits - don't include your country code / initial 0.");
                }

                // BLOCK 15: WHEN N0 DETAILS HAVE BEEN FILLED OUT

            } else {
                throw new NullPointerException("Account details not saved. Please check at least one of the fields are filled out.");
                }

            // BLOCK 16: WHEN USER IS INCORRECT / NOT LOGGED IN (POINTLESS BUT MEH!)

        } else {
            throw new NullPointerException("User not found. Please ensure details have been correctly entered.");
        }
    }


    public String addBankAccount (String inputAccountOption, int customerNumber) throws RuntimeException {

        BankAccount bankAccountOption = bankAccountRepository.findAccountTypeByName(inputAccountOption);
        AccountUser existingUser = accountUserRepository.findUserByCustomerNumber(customerNumber);

        return null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
}


//if (bankAccountOption != null && existingUser != null) {
//        try {
//        if (existingUser.getBankAccount().contains(bankAccountOption)){
//        throw new RuntimeException("You already have a " + bankAccountOption.getName() + " Account, " + existingUser.getFirstName() + " " + existingUser.getLastName() + "!");
//        } else {
//        userBankAccount.add(bankAccountOption);
//        accountUserRepository.save(existingUser);
//        return "Bank Account " + bankAccountOption + " added.";
//        }
//        } catch (Exception e){
//        e.printStackTrace();
//        }
//        } else if (bankAccountOption == null && existingUser != null) {
//        throw new RuntimeException("Sorry, we can't provide you with this account as it either 1) doesn't exist or 2) you are not eligible. Please try again.");
//        } else {
//        throw new NullPointerException("User not found. Please ensure details have been correctly entered.");
//        }
//        return "Process complete.";