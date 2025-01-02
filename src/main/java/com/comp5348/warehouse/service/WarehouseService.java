package com.comp5348.warehouse.service;

import com.comp5348.warehouse.model.OrderAllocation;
import com.comp5348.warehouse.model.WarehouseProduct;
import com.comp5348.warehouse.repository.WarehouseProductRepository;
import com.comp5348.warehouse.repository.OrderAllocationRepository;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Log
@Service
public class WarehouseService {

    private final WarehouseProductRepository warehouseProductRepository;
    private final OrderAllocationRepository orderAllocationRepository;

    @Autowired
    public WarehouseService(WarehouseProductRepository warehouseProductRepository, OrderAllocationRepository orderAllocationRepository) {
        this.warehouseProductRepository = warehouseProductRepository;
        this.orderAllocationRepository = orderAllocationRepository;
    }

    // Get total stock for a specific product
    public int getTotalStock(Long productId) {
        List<WarehouseProduct> productsInWarehouses = warehouseProductRepository.findByProductId(productId);
        return productsInWarehouses.stream()
                .mapToInt(WarehouseProduct::getStockLevels)
                .sum();
    }

    // Update stock levels for a specific product
    @Transactional
    public boolean updateProductStock(Long productId, int quantityChange) {
        List<WarehouseProduct> productsInWarehouses = warehouseProductRepository.findByProductId(productId);

        if (productsInWarehouses.isEmpty()) {
            return false;
        }

        // Distribute stock changes evenly across warehouses (for demonstration purposes)
        int warehousesCount = productsInWarehouses.size();
        int quantityPerWarehouse = quantityChange / warehousesCount;
        int remainder = quantityChange % warehousesCount;

        for (WarehouseProduct product : productsInWarehouses) {
            int newStockLevel = product.getStockLevels() + quantityPerWarehouse + (remainder > 0 ? 1 : 0);
            remainder--;

            if (newStockLevel < 0) {
                return false; // Stock cannot be negative
            }

            product.setStockLevels(newStockLevel);
            warehouseProductRepository.save(product);
        }

        return true;
    }

    // Get all product stock details
    public List<WarehouseProduct> getAllProductStocks() {
        return warehouseProductRepository.findAll();
    }

    // Check if stock is available for a product
    public boolean isStockAvailable(Long productId, int requiredQuantity) {
        int totalQuantity = getTotalStock(productId);
        return totalQuantity >= requiredQuantity;
    }

    // Allocate stock (reduce stock levels)
    @Transactional
    public boolean allocateStock(Long orderId, Long productId, int requiredQuantity) {
        List<WarehouseProduct> productsInWarehouses = warehouseProductRepository.findByProductId(productId);

        // Sort by stock levels in descending order
        productsInWarehouses.sort((p1, p2) -> Integer.compare(p2.getStockLevels(), p1.getStockLevels()));

        int remainingQuantity = requiredQuantity;

        for (WarehouseProduct product : productsInWarehouses) {
            if (remainingQuantity <= 0) break;

            int availableQuantity = product.getStockLevels();
            int deductedQuantity = Math.min(availableQuantity, remainingQuantity);

            // Reduce stock and save
            product.setStockLevels(availableQuantity - deductedQuantity);
            warehouseProductRepository.save(product);

            // Create order allocation record
            OrderAllocation allocation = new OrderAllocation(orderId, productId, product.getWarehouse().getId(), deductedQuantity);
            orderAllocationRepository.save(allocation);

            remainingQuantity -= deductedQuantity;
        }

        // Return whether all required stock was allocated
        return remainingQuantity <= 0;
    }

    // Release allocated stock back into the inventory
    @Transactional
    public void releaseStock(Long orderId) {
        List<OrderAllocation> allocations = orderAllocationRepository.findByOrderId(orderId);
        for (OrderAllocation allocation : allocations) {
            WarehouseProduct warehouseProduct = warehouseProductRepository
                    .findByWarehouseIdAndProductId(allocation.getWarehouseId(), allocation.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Inventory not found for warehouseId=" + allocation.getWarehouseId() +
                                    " and productId=" + allocation.getProductId()));

            warehouseProduct.setStockLevels(warehouseProduct.getStockLevels() + allocation.getAllocatedQuantity());
            warehouseProductRepository.save(warehouseProduct);

            // Delete order allocation record
            orderAllocationRepository.delete(allocation);

            log.info("Increased stock for Product ID: " + allocation.getProductId() +
                    " in Warehouse ID: " + allocation.getWarehouseId() +
                    " by " + allocation.getAllocatedQuantity());
        }
    }
}
