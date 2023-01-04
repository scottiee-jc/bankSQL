package com.example.da_bank.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "account_holders")
public class AccountUser implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "first_name")
    String firstName;
    @Column(name = "last_name")
    String lastName;
    @Column(name = "customer_number")
    int customerNumber;
    String dob;
    @Column(nullable = false, length = 50, unique = true)
    String username;
    @Column(nullable = false, length = 64)
    String password;
    String email;
    String phone_number;
    @OneToMany
    @JoinColumn(name = "bank_account_id")
    @JsonIgnoreProperties
    private List<BankAccount> bankAccount;

    @ManyToMany(fetch = FetchType.EAGER)
    private Collection<Role> roles = new ArrayList<>();

    public AccountUser() {
    }

    public AccountUser(Long id, String firstName, String lastName, int customerNumber, String dob, String username, String password, String email, String phone_number) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.customerNumber = customerNumber;
        this.dob = dob;
        this.username = username;
        this.password = password;
        this.bankAccount = new ArrayList<>();
        this.email = email;
        this.phone_number = phone_number;
        this.roles = new ArrayList<>();
    }

    public AccountUser(String firstName, String lastName, int customerNumber, String dob, String username, String password, String email, String phone_number) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.customerNumber = customerNumber;
        this.dob = dob;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone_number = phone_number;
    }

    // TESTING CONSTRUCTORS ---------------------------------------------------------

    public AccountUser(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public AccountUser(String username, String password, int customerNumber) {
        this.username = username;
        this.password = password;
        this.customerNumber = customerNumber;
    }

//  --------------------------------------------------------------------------------

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getRoleName()));
        }
        return authorities;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(int customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    @Override
    public String getUsername(){
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<BankAccount> getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(List<BankAccount> bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public Collection<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public void addRole(Role role){
        this.roles.add(role);
    }
}
