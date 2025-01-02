package com.comp5348.bank.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Entity object for customer database table.
 */
@Getter
@NoArgsConstructor
@Entity
public class Customer {
    @Id
    @GeneratedValue
    private long id;

    @Version
    private int version;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @OneToMany(mappedBy = "customer")
    private final Collection<Account> accounts = new ArrayList<>();

    public Customer(String af, String al) {
        this.firstName = af;
        this.lastName = al;
    }
}
