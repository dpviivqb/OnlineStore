package com.comp5348.warehouse.controller;

import com.comp5348.warehouse.model.WarehouseProduct;
import com.comp5348.warehouse.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/warehouse")
public class WarehouseController {

    private final WarehouseService warehouseService;

    @Autowired
    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    /**
     * Query product stock.
     */
    @GetMapping("/stock/{productId}")
    public ResponseEntity<Integer> getProductStock(@PathVariable Long productId) {
        int totalStock = warehouseService.getTotalStock(productId);
        return ResponseEntity.ok(totalStock);
    }

    /**
     * Update product stock.
     */
    @PutMapping("/stock/{productId}")
    public ResponseEntity<String> updateProductStock(@PathVariable Long productId, @RequestParam int quantity) {
        boolean updated = warehouseService.updateProductStock(productId, quantity);
        if (updated) {
            return ResponseEntity.ok("Stock updated successfully.");
        } else {
            return ResponseEntity.status(400).body("Failed to update stock.");
        }
    }

    /**
     * Get all product stock details.
     */
    @GetMapping("/stocks")
    public ResponseEntity<List<WarehouseProduct>> getAllProductStocks() {
        List<WarehouseProduct> stocks = warehouseService.getAllProductStocks();
        return ResponseEntity.ok(stocks);
    }
}
