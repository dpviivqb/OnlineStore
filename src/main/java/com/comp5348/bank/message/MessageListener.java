package com.comp5348.bank.message;

import com.comp5348.bank.config.RabbitMQConfig;
import com.comp5348.bank.dto.TransactionRecordDTO;
import com.comp5348.bank.errors.PaymentFailedException;
import com.comp5348.bank.service.TransactionRecordService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.java.Log;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.Map;

@Log
@Component
public class MessageListener {

    private final TransactionRecordService transactionRecordService;
    private final MessageSender messageSender;
    private final ObjectMapper objectMapper;

    public MessageListener(TransactionRecordService transactionRecordService, MessageSender messageSender) {
        this.transactionRecordService = transactionRecordService;
        this.messageSender = messageSender;
        this.objectMapper = new ObjectMapper();
    }

    // Listener method that only receives messages and calls the method with retry logic
    @RabbitListener(queues = RabbitMQConfig.PAYMENT_REQUEST_QUEUE)
    public void handlePaymentRequest(String message) {
        try {
            retryableHandlePayment(message); // Call the method with @Retryable
        } catch (PaymentFailedException e) {
            log.severe("Payment failed and recover method will be triggered: " + e.getMessage());
        }
    }

    // Method with retry logic
    @Retryable(
            value = {PaymentFailedException.class}, // Type of exception to retry
            maxAttempts = 3, // Maximum retry attempts
            backoff = @Backoff(delay = 5000) // Delay of 5 seconds between retries
    )
    public void retryableHandlePayment(String message) throws PaymentFailedException {
        handlePaymentLogic(message); // Call the actual payment logic
    }

    // The actual payment logic method
    private void handlePaymentLogic(String message) throws PaymentFailedException {
        Long orderId = null;
        boolean isSuccess = false;

        try {
            // Parse message content
            Map<String, Object> request = objectMapper.readValue(message, Map.class);
            orderId = Long.valueOf(request.get("orderId").toString());
            Long fromCustomerId = Long.valueOf(request.get("fromCustomerId").toString());
            Long fromAccountId = Long.valueOf(request.get("fromAccountId").toString());
            Long toCustomerId = Long.valueOf(request.get("toCustomerId").toString());
            Long toAccountId = Long.valueOf(request.get("toAccountId").toString());
            Double amount = Double.valueOf(request.get("amount").toString());
            String memo = request.getOrDefault("memo", "").toString();

            log.info("Received payment request for Order ID: " + orderId);

            // Simulate 5% failure rate
            if (!simulatePayment()) {
                throw new PaymentFailedException("Simulated payment failure");
            }

            // Execute the transaction
            TransactionRecordDTO transactionRecordDTO = transactionRecordService.performTransaction(
                    fromCustomerId, fromAccountId,
                    toCustomerId, toAccountId,
                    amount, memo
            );

            // Transaction successful
            isSuccess = true;
            log.info(String.format("Payment processing completed for orderId=%d with status=%s",
                    orderId, "SUCCESS"));

        } catch (Exception e) {
            log.severe("Failed to handle payment request: " + e.getMessage());
            // Transaction failed
            isSuccess = false;
            throw new PaymentFailedException(e.getMessage()); // Throw exception to trigger retry
        } finally {
            // Send payment response regardless of success or failure
            if (orderId != null) {
                messageSender.sendPaymentResponse(orderId, isSuccess);
            } else {
                log.severe("Order ID is null, cannot send payment response");
            }
        }
    }

    // Method called when retry attempts exceed the maximum number
    @Recover
    public void recover(PaymentFailedException e, String message) {
        try {
            Map<String, Object> request = objectMapper.readValue(message, Map.class);
            Long orderId = Long.valueOf(request.get("orderId").toString());
            log.severe("Payment failed after retries for order ID: " + orderId + ", Error: " + e.getMessage());

            // Send payment failure response
            messageSender.sendPaymentResponse(orderId, false);

        } catch (Exception ex) {
            log.severe("Failed to handle recovery: " + ex.getMessage());
        }
    }

    // Simulate payment logic with a 5% failure rate
    private boolean simulatePayment() {
        return Math.random() >= 0.05; // 95% success rate, 5% failure rate
    }

    @RabbitListener(queues = RabbitMQConfig.REFUND_REQUEST_QUEUE)
    public void handleRefundRequest(String message) {
        Long orderId = null;
        boolean isSuccess = false;
        try {
            Map<String, Object> request = objectMapper.readValue(message, Map.class);
            orderId = Long.valueOf(request.get("orderId").toString());
            Long fromCustomerId = Long.valueOf(request.get("fromCustomerId").toString());
            Long fromAccountId = Long.valueOf(request.get("fromAccountId").toString());
            Long toCustomerId = Long.valueOf(request.get("toCustomerId").toString());
            Long toAccountId = Long.valueOf(request.get("toAccountId").toString());
            Double amount = Double.valueOf(request.get("amount").toString());
            String memo = request.getOrDefault("memo", "").toString();

            log.info("Received refund request for Order ID: " + orderId);

            // Process refund
            TransactionRecordDTO transactionRecordDTO = transactionRecordService.performTransaction(
                    fromCustomerId, fromAccountId,
                    toCustomerId, toAccountId,
                    amount, memo);

            // Refund successful if no exception is thrown
            isSuccess = true;

            log.info(String.format("Refund processing completed for orderId=%d with status=%s",
                    orderId, isSuccess ? "SUCCESS" : "FAILED"));

        } catch (Exception e) {
            log.severe("Failed to handle refund request: " + e.getMessage());
            // Refund failed
            isSuccess = false;
        } finally {
            // Send refund response regardless of success or failure
            if (orderId != null) {
                messageSender.sendRefundResponse(orderId, isSuccess);
            } else {
                log.severe("Order ID is null, cannot send refund response");
            }
        }
    }
}
