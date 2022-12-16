package com.example.da_bank.controllers;

import com.example.da_bank.models.AccountUser;
import com.example.da_bank.repositories.ContactRepository;
import com.example.da_bank.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

@RestController
public class ContactsController {

    @Autowired
    ContactRepository contactRepository;

    @Autowired
    ContactService contactService;

    public ContactsController(ContactRepository contactRepository, ContactService contactService) {
        this.contactRepository = contactRepository;
        this.contactService = contactService;
    }


    @PostMapping("/addContact")
    public @ResponseBody String addContact(
            @RequestParam (name = "username_a") String username_a,
            @RequestParam (name = "username_b") String username_b
    ){
        return contactService.addContact(username_a, username_b);
    }

    @DeleteMapping("/deleteContact")
    public @ResponseBody String deleteContact(
            @RequestParam (name = "username_a") String username_a,
            @RequestParam (name = "username_b") String username_b
    ){
        return contactService.deleteContact(username_a, username_b);
    }
}
