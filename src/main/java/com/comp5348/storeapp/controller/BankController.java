package com.comp5348.storeapp.controller;

import com.comp5348.storeapp.service.BankService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bank")
public class BankController {

    private final BankService bankService;

    public BankController(BankService bankService) {
        this.bankService = bankService;
    }

    @PostMapping("/payment/{orderId}")
    public ResponseEntity<String> initiatePayment(@PathVariable Long orderId) {
        bankService.sendPaymentRequest(orderId);
        return ResponseEntity.ok("Payment request sent for order ID: " + orderId);
    }

    @PostMapping("/refund/{orderId}")
    public ResponseEntity<String> initiateRefund(@PathVariable Long orderId) {
        bankService.sendRefundRequest(orderId);
        return ResponseEntity.ok("Refund request sent for order ID: " + orderId);
    }
}
