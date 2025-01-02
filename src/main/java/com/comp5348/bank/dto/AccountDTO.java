package com.comp5348.bank.dto;

import com.comp5348.bank.model.Account;
import com.comp5348.bank.model.TransactionRecord;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * Data Transfer Object for Account.
 */
@Getter
@Setter
@JsonInclude(Include.NON_EMPTY)
public class AccountDTO {
    private Long id;
    private CustomerDTO customer;
    private String name;
    private Double balance;

    /**
     * The set of transaction records associated with the account.
     */
    private Set<TransactionRecordDTO> transactionRecords = new HashSet<>();


    /**
     * Constructs an AccountDTO from an Account entity.
     *
     * @param accountEntity the account entity
     */
    public AccountDTO(Account accountEntity) {
        this(accountEntity, false);
    }

    /**
     * Constructs an AccountDTO from an Account entity.
     *
     * @param accountEntity          the account entity
     * @param includeRelatedEntities whether to include related entities
     */
    public AccountDTO(Account accountEntity, boolean includeRelatedEntities) {
        this.id = accountEntity.getId();
        this.balance = accountEntity.getBalance();
        this.name = accountEntity.getName();

        if (includeRelatedEntities) {
            this.customer = new CustomerDTO(accountEntity.getCustomer());
            for (TransactionRecord fromTransactionRecord : accountEntity.getFromTransactionRecords()) {
                transactionRecords.add(new TransactionRecordDTO(fromTransactionRecord));
            }
            for (TransactionRecord toTransactionRecord : accountEntity.getToTransactionRecords()) {
                transactionRecords.add(new TransactionRecordDTO(toTransactionRecord));
            }
        }
    }
}