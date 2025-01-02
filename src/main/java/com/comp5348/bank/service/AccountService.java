package com.comp5348.bank.service;

import com.comp5348.bank.dto.AccountDTO;
import com.comp5348.bank.model.Account;
import com.comp5348.bank.model.Customer;
import com.comp5348.bank.repository.AccountRepository;
import com.comp5348.bank.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Business logic for account management.
 */
@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository, CustomerRepository customerRepository) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public AccountDTO createAccount(Long customerId, String name) {
        Customer customer = customerRepository.getReferenceById(customerId);
        Account account = new Account(customer, name);
        account = accountRepository.save(account);
        return new AccountDTO(account, true);
    }

    @Transactional
    public AccountDTO getAccount(Long customerId, Long accountId) {
        Account account = accountRepository.findByIdAndCustomer(accountId, customerRepository.getReferenceById(customerId)).orElseThrow();
        return new AccountDTO(account, true);
    }
}
