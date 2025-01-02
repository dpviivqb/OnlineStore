package com.comp5348.storeapp.controller;

import com.comp5348.storeapp.service.WarehouseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/warehouse")
public class WarehouseController {

    private final WarehouseService warehouseService;

    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @PostMapping("/checkStock")
    public ResponseEntity<String> checkStock(@RequestBody StockCheckRequest request) {
        warehouseService.checkStock(
                request.orderId,
                request.productId,
                request.quantity
        );
        return ResponseEntity.ok(
                "Warehouse stock check requested for order ID: " + request.orderId
        );
    }

    @PostMapping("/releaseStock/{orderId}")
    public ResponseEntity<String> releaseStock(@PathVariable Long orderId) {
        warehouseService.releaseStock(orderId);
        return ResponseEntity.ok("Warehouse stock release requested for order ID: " + orderId);
    }

    public static class StockCheckRequest{
        public Long orderId;
        public Long productId;
        public int quantity;
    }
}

