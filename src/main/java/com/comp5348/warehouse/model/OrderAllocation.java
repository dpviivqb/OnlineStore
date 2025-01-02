package com.comp5348.warehouse.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_allocations")
@Data
@NoArgsConstructor
public class OrderAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long orderId;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Long warehouseId;

    @Column(nullable = false)
    private int allocatedQuantity;

    @Version
    private int version;

    public OrderAllocation(Long orderId, Long productId, Long warehouseId, int allocatedQuantity) {
        this.orderId = orderId;
        this.productId = productId;
        this.warehouseId = warehouseId;
        this.allocatedQuantity = allocatedQuantity;
    }
}

