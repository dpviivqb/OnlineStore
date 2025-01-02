package com.comp5348.deliveryCo.controller;

import com.comp5348.deliveryCo.model.Delivery;
import com.comp5348.deliveryCo.service.DeliveryService;
import lombok.extern.java.Log;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log
@RestController
@RequestMapping("/api/deliveries")
public class DeliveryController {

    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Delivery> getDelivery(@PathVariable Long orderId) {
        try {
            Delivery delivery = deliveryService.getDeliveryByOrderId(orderId);
            return ResponseEntity.ok(delivery);
        } catch (IllegalArgumentException e) {
            log.warning("Delivery not found for orderId=" + orderId);
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<String> updateDeliveryStatus(@PathVariable Long orderId,
                                                       @RequestBody UpdateStatusRequest request) {
        try {
            deliveryService.updateDeliveryStatus(orderId, request.status);
            return ResponseEntity.ok("Delivery status updated for orderId=" + orderId);
        } catch (IllegalArgumentException e) {
            log.warning("Failed to update delivery status for orderId=" + orderId);
            return ResponseEntity.notFound().build();
        }
    }

    public static class UpdateStatusRequest {
        private String status;

    }
}
