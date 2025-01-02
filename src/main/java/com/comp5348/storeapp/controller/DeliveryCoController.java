package com.comp5348.storeapp.controller;

import com.comp5348.storeapp.service.DeliveryCoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/delivery")
public class DeliveryCoController {

    private final DeliveryCoService deliveryCoService;

    public DeliveryCoController(DeliveryCoService deliveryCoService) {
        this.deliveryCoService = deliveryCoService;
    }

    @PostMapping("/request/{orderId}")
    public ResponseEntity<String> requestDelivery(@PathVariable Long orderId) {
        deliveryCoService.sendDeliveryRequest(orderId);
        return ResponseEntity.ok("Delivery request sent for order ID: " + orderId);
    }

    @PostMapping("/updateStatus")
    public ResponseEntity<String> updateDeliveryStatus(@RequestParam Long orderId, @RequestParam String deliveryStatus) {
        deliveryCoService.updateDeliveryStatus(orderId, deliveryStatus);
        return ResponseEntity.ok("Delivery status updated for order ID: " + orderId);
    }
}
