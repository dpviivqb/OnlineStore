package com.comp5348.storeapp.event;

import lombok.Getter;

@Getter
public class WarehouseStockReleaseRequestEvent {
    private final Long orderId;

    public WarehouseStockReleaseRequestEvent(Long orderId) {
        this.orderId = orderId;
    }
}
