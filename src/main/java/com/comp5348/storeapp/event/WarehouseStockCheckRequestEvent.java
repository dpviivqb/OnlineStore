package com.comp5348.storeapp.event;

import lombok.Getter;

@Getter
public class WarehouseStockCheckRequestEvent {

    private final Long orderId;
    private final Long productId;
    private final int quantity;

    public WarehouseStockCheckRequestEvent(Long orderId, Long productId, int quantity) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
    }
}
