package com.comp5348.bank.dto;

import com.comp5348.bank.model.Account;
import com.comp5348.bank.model.Customer;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Data Transfer Object for Customer.
 */
@RequiredArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private Collection<AccountDTO> accounts = new ArrayList<>();

    public CustomerDTO(Customer customerEntity) {
        this.id = customerEntity.getId();
        this.firstName = customerEntity.getFirstName();
        this.lastName = customerEntity.getLastName();
        for (Account account : customerEntity.getAccounts()) {
            this.accounts.add(new AccountDTO(account));
        }
    }
}
