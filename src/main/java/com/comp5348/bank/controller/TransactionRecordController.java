package com.comp5348.bank.controller;

import com.comp5348.bank.dto.TransactionRecordDTO;
import com.comp5348.bank.service.TransactionRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer/{fromCustomerId}/account/{accountId}/transaction_record")
public class TransactionRecordController {

    private final TransactionRecordService transactionRecordService;

    @Autowired
    public TransactionRecordController(TransactionRecordService transactionRecordService) {
        this.transactionRecordService = transactionRecordService;
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@PathVariable Long fromCustomerId,
                                      @PathVariable("accountId") Long fromAccountId,
                                      @RequestBody TransferRequest request) {
        TransactionRecordDTO transactionRecord = transactionRecordService
                .performTransaction(fromCustomerId, fromAccountId,
                        request.toCustomerId, request.toAccountId,
                        request.amount, "Transfer.");
        return ResponseEntity.ok(transactionRecord);
    }

    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(@PathVariable("fromCustomerId") Long toCustomerId,
                                     @PathVariable("accountId") Long toAccountId,
                                     @RequestBody DepositWithdrawRequest request) {
        TransactionRecordDTO transactionRecord = transactionRecordService
                .performTransaction(null, null,
                        toCustomerId, toAccountId,
                        request.amount, "Deposit.");
        return ResponseEntity.ok(transactionRecord);
    }

    public static class TransferRequest {
        public long toCustomerId;
        public long toAccountId;
        public double amount;
    }

    public static class DepositWithdrawRequest {
        public double amount;
    }
}
