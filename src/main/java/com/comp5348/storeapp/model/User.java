package com.comp5348.storeapp.model;

import com.comp5348.bank.model.Account;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;


@Entity
@NoArgsConstructor
@Data
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password; // Hashed password

    @Column(nullable = false, unique = true)
    private String email;

    // New fields for bank account association
    @Column
    private Long bankCustomerId; // Bank application's customer ID

    @Column
    private Long bankAccountId; // Bank application's account ID

    @Version
    private int version;

    // Existing constructor
    public User(String username, String encodedPassword, String email) {
        this.username = username;
        this.password = encodedPassword;
        this.email = email;
    }
}

