package com.comp5348.storeapp.controller;

import com.comp5348.storeapp.model.DeliveryStatus;
import com.comp5348.storeapp.model.Order;
import com.comp5348.storeapp.model.PaymentStatus;
import com.comp5348.storeapp.service.OrderService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Log
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Create a new order
     */
    @PostMapping
    public ResponseEntity<String> createOrder(@RequestBody OrderRequest request) {
        log.info("Received request to create order for user: " + request.username);
        String result = orderService.createOrder(request.username, request.productId, request.quantity);
        log.info("Order creation result: " + result);
        return ResponseEntity.ok(result);
    }

    /**
     * Retrieve an order
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrder(@PathVariable Long orderId) {
        log.info("Fetching order with ID: " + orderId);
        Optional<Order> orderOptional = orderService.getOrderById(orderId);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            log.info("Order found: " + order);
            return ResponseEntity.ok(order);
        } else {
            log.warning("Order not found with ID: " + orderId);
            return ResponseEntity.status(404).body("Order not found.");
        }
    }

    /**
     * Cancel an order
     */
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<String> cancelOrder(@PathVariable Long orderId) {
        log.info("Attempting to cancel order with ID: " + orderId);
        boolean cancelled = orderService.cancelOrder(orderId);
        if (cancelled) {

            log.info("Order cancelled successfully with ID: " + orderId);
            return ResponseEntity.ok("Order cancelled successfully.");
        } else {
            log.warning("Failed to cancel order with ID: " + orderId);
            return ResponseEntity.status(404).body("Order not found or cannot be cancelled.");
        }
    }

    public static class OrderRequest {
        public String username;
        public Long productId;
        public int quantity;
    }

}
