package com.comp5348.bank.repository;

import com.comp5348.bank.model.TransactionRecord;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Data Access Object for transaction_record database table.
 */
public interface TransactionRecordRepository extends JpaRepository<TransactionRecord, Long> {
}
