package com.example.da_bank.models;

import javax.persistence.*;

@Entity
@Table(name="roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50, unique = true)
    private String roleName;

    public Role(){
    }

    public Role(String roleName, Long id){
        this.roleName = roleName;
        this.id = id;
    }

    public Role(String roleName) {
        this.roleName = roleName;
    }
    //test constructor
    public Role(Long roleId) {
    }
    //test constructor

    @Override
    public String toString() {
        return this.roleName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
