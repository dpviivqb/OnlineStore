package com.comp5348.bank.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Entity object for account database table.
 */
@Getter
@NoArgsConstructor
@Entity
public class Account {
    @Id
    @GeneratedValue
    private long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Customer customer;

    @Version
    private int version;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double balance = 0.0;

    @OneToMany(mappedBy = "fromAccount")
    private Collection<TransactionRecord> fromTransactionRecords;

    @OneToMany(mappedBy = "toAccount")
    private Collection<TransactionRecord> toTransactionRecords;

    public Account(Customer customer, String name) {
        this.customer = customer;
        this.name = name;
        this.fromTransactionRecords = new ArrayList<>();
        this.toTransactionRecords = new ArrayList<>();
    }

    public void modifyBalance(Double amount) {
        this.balance = this.balance + amount;
    }
}
