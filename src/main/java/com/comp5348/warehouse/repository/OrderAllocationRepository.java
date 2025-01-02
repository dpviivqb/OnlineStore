package com.comp5348.warehouse.repository;

import com.comp5348.warehouse.model.OrderAllocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderAllocationRepository extends JpaRepository <OrderAllocation, Long> {
    List<OrderAllocation> findByOrderId(Long orderId);
}


