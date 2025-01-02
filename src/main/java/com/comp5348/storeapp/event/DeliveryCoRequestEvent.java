package com.comp5348.storeapp.event;

import lombok.Getter;

@Getter
public class DeliveryCoRequestEvent {

    private final Long orderId;

    public DeliveryCoRequestEvent(Long orderId) {
        this.orderId = orderId;
    }
}
