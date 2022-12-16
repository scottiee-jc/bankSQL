package com.example.da_bank.models;

import javax.persistence.*;

@Entity
@Table(name = "contacts")
public class Contacts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "contact_a_id")
    private AccountUser contact_a;

    @ManyToOne
    @JoinColumn(name = "contact_b_id")
    private AccountUser contact_b;

    @Column(name = "username_a")
    private String username_a;

    @Column(name = "username_b")
    private String username_b;

    public Contacts(Long id, AccountUser contact_a, AccountUser contact_b, String username_a, String username_b) {
        this.id = id;
        this.contact_a = contact_a;
        this.contact_b = contact_b;
        this.username_a = username_a;
        this.username_b = username_b;
    }

    public Contacts() {
    }

    // contact service
    public Contacts(AccountUser contact_a, AccountUser contact_b, String username_a, String username_b) {
        this.contact_a = contact_a;
        this.contact_b = contact_b;
        this.username_a = username_a;
        this.username_b = username_b;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AccountUser getContact_a() {
        return contact_a;
    }

    public void setContact_a(AccountUser contact_a) {
        this.contact_a = contact_a;
    }

    public AccountUser getContact_b() {
        return contact_b;
    }

    public void setContact_b(AccountUser contact_b) {
        this.contact_b = contact_b;
    }

    public String getUsername_a() {
        return username_a;
    }

    public void setUsername_a(String username_a) {
        this.username_a = username_a;
    }

    public String getUsername_b() {
        return username_b;
    }

    public void setUsername_b(String username_b) {
        this.username_b = username_b;
    }
}
