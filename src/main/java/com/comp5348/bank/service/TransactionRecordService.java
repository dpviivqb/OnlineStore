package com.comp5348.bank.service;

import com.comp5348.bank.dto.TransactionRecordDTO;
import com.comp5348.bank.errors.InsufficientBalanceException;
import com.comp5348.bank.errors.NegativeTransferAmountException;
import com.comp5348.bank.model.Account;
import com.comp5348.bank.model.TransactionRecord;
import com.comp5348.bank.repository.AccountRepository;
import com.comp5348.bank.repository.CustomerRepository;
import com.comp5348.bank.repository.TransactionRecordRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

/**
 * Business logic for creating and managing transactions (transfer / deposit).
 */
@Service
public class TransactionRecordService {
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final TransactionRecordRepository transactionRecordRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public TransactionRecordService(AccountRepository accountRepository, CustomerRepository customerRepository, TransactionRecordRepository transactionRecordRepository) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
        this.transactionRecordRepository = transactionRecordRepository;
    }

    @Transactional
    public TransactionRecordDTO performTransaction(
            Long fromCustomerId, Long fromAccountId,
            Long toCustomerId, Long toAccountId,
            Double amount, String memo)
            throws InsufficientBalanceException, NegativeTransferAmountException, HttpClientErrorException {
        if (amount <= 0) {
            throw new NegativeTransferAmountException();
        }

        Account fromAccount = null;
        if (fromAccountId != null) {
            fromAccount = accountRepository
                    .findByIdAndCustomer(fromAccountId, customerRepository.getReferenceById(fromCustomerId))
                    .orElseThrow();
            entityManager.refresh(fromAccount);

            if (fromAccount.getBalance() < amount) {
                throw new InsufficientBalanceException();
            }
            fromAccount.modifyBalance(-amount);
            accountRepository.save(fromAccount);
        }
        Account toAccount = null;
        if (toAccountId != null) {
            toAccount = accountRepository
                    .findByIdAndCustomer(toAccountId, customerRepository.getReferenceById(toCustomerId))
                    .orElseThrow();
            toAccount.modifyBalance(amount);
            accountRepository.save(toAccount);
        }

        TransactionRecord transactionRecord = new TransactionRecord(amount, toAccount, fromAccount, memo);
        transactionRecordRepository.save(transactionRecord);

        return new TransactionRecordDTO(transactionRecord);
    }
}
