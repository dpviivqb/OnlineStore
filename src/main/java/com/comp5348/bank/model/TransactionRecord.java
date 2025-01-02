package com.comp5348.bank.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity object for transaction_record database table.
 */
@Getter
@NoArgsConstructor
@Entity
public class TransactionRecord {
    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false)
    private Double amount;

    private String memo;

    @Column(nullable = false)
    private LocalDateTime time;

    @ManyToOne
    @JoinColumn
    private Account toAccount;

    @ManyToOne
    @JoinColumn
    private Account fromAccount;

    @Version
    private int version;

    public TransactionRecord(Double amount, Account toAccount, Account fromAccount, String memo) {
        this.amount = amount;
        this.time = LocalDateTime.now();
        this.toAccount = toAccount;
        this.fromAccount = fromAccount;
        this.memo = memo;
    }
}
