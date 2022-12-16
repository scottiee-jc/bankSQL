package com.example.da_bank.models;

import javax.persistence.*;

@Entity
@Table(name="roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50, unique = true)
    private String name;

    public Role(){
    }

    public Role(String name, Long id){
        this.name = name;
        this.id = id;
    }

    public Role(String name) {
        this.name = name;
    }
    //test constructor
    public Role(Long roleId) {
    }
    //test constructor

    @Override
    public String toString() {
        return this.name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
