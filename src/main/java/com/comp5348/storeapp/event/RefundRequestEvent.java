package com.comp5348.storeapp.event;

import com.comp5348.storeapp.model.Order;
import lombok.Getter;

@Getter
public class RefundRequestEvent {
    private final Order order;

    public RefundRequestEvent(Order order) {
        this.order = order;
    }
}