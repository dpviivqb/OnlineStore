package com.comp5348.bank.dto;

import com.comp5348.bank.model.Account;
import com.comp5348.bank.model.TransactionRecord;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for TransactionRecord.
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionRecordDTO {
    private long id;
    private Double amount;
    private String memo;
    private LocalDateTime time;
    private AccountDTO toAccount;
    private AccountDTO fromAccount;

    public TransactionRecordDTO(TransactionRecord entity) {
        this.id = entity.getId();
        this.memo = entity.getMemo();
        this.amount = entity.getAmount();
        this.time = entity.getTime();
        Account toAccount = entity.getToAccount();
        if (toAccount != null) {
            this.toAccount = new AccountDTO(toAccount);
        }
        Account fromAccount = entity.getFromAccount();
        if (fromAccount != null) {
            this.fromAccount = new AccountDTO(fromAccount);
        }
    }
}
