package com.comp5348.warehouse.repository;

import com.comp5348.warehouse.model.WarehouseProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseProductRepository extends JpaRepository<WarehouseProduct, Long> {

    // Find all related products based on warehouse ID
    List<WarehouseProduct> findByWarehouseId(Long warehouseId);

    // Find all warehouses storing the product based on the product ID
    List<WarehouseProduct> findByProductId(Long productId);

    // Find the unique product inventory record based on the warehouse ID and product ID
    Optional<WarehouseProduct> findByWarehouseIdAndProductId(Long warehouseId, Long productId);
}
