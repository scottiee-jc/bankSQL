package com.example.da_bank.service;

import com.example.da_bank.models.AccountUser;
import com.example.da_bank.models.Contacts;
import com.example.da_bank.repositories.AccountUserRepository;
import com.example.da_bank.repositories.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.stereotype.Service;

@Service
public class ContactService {

    @Autowired
    ContactRepository contactRepository;

    @Autowired
    AccountUserRepository accountUserRepository;

    public ContactService(ContactRepository contactRepository, AccountUserRepository accountUserRepository) {
        this.contactRepository = contactRepository;
        this.accountUserRepository = accountUserRepository;
    }

    public String addContact(String user1, String user2){

        AccountUser userA = accountUserRepository.findUserByUsername(user1);
        AccountUser userB = accountUserRepository.findUserByUsername(user2);

        // Creating two new Users...
        // userA corresponds to the current user. user1 should correspond to whoever is logged in
        // userB corresponds to the TARGET user to be added as a contact
        // 1A, 2B

        Contacts newContact = new Contacts(userA, userB, user1, user2);

        // Create a new Contact object to add the users and their usernames

        Contacts existingContact1 = contactRepository.findContactPair(user1, user2);
        Contacts existingContact2 = contactRepository.findContactPair(user2, user1);

        // Create two combinations to check that the inputs are checked in both columns in the friend table


        if (userB == null) {
            throw new InvalidDataAccessApiUsageException("User does not exist! Check the details are right and try again, " + userA.getFirstName() + " " + userB.getLastName());
            // If userF is null , i.e the entered friendToAddUsername does not correspond to an existing user, then it will
            // throw an invalidDataAccessApiUsageException as shown above
        } else if (userA == userB) {
            throw new InvalidDataAccessApiUsageException("You can't add yourself as a Contact, " + userA.getFirstName() + ".");
            // If userC = userF (both inputs load the same user) then it will throw an invalidDataAccessApiUsageException as shown above
        } else if (existingContact1 == null && existingContact2 == null) {
            contactRepository.save(newContact);
            // if neither combination returns an existing friend object, save the new friend object
        } else {
            throw new InvalidDataAccessApiUsageException("You're already got " + userB.getFirstName() + " as a contact!");
            // otherwise, you'll be friends with the friend to add, so
            // an invalidDataAccessApiUsageException will be thrown as shown above
        }
        return "You have added " + userB.getFirstName() + " " + userB.getLastName() + " to your contacts list, " + userA.getFirstName() + ". If you want to send them money, you can do this now from your account!";
    }

    public String deleteContact (String username_a, String username_b) throws NullPointerException {

        Contacts contactCombo1 = contactRepository.findContactPair(username_a, username_b);
        Contacts contactCombo2 = contactRepository.findContactPair(username_b, username_a);

        if (contactCombo1 != null && contactCombo2 == null) {
            contactRepository.delete(contactCombo1);
            return "Contact " + contactCombo1.getContact_b().getFirstName() + " " + contactCombo1.getContact_b().getLastName() + " deleted. If you want to add them back, you can do so through the 'add contact' feature";
        } else if (contactCombo2 != null && contactCombo1 == null) {
            contactRepository.delete(contactCombo2);
            return "Contact " + contactCombo2.getContact_a().getFirstName() + " " + contactCombo2.getContact_a().getLastName() + " deleted. If you want to add them back, you can do so through the 'add contact' feature";
        } else {
            throw new NullPointerException("Error: Contact doesn't exist. Please ensure you entered the correct information.");
        }

    }
}
