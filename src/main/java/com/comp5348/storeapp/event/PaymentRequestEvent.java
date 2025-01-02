package com.comp5348.storeapp.event;

import com.comp5348.storeapp.model.Order;
import lombok.Getter;

@Getter
public class PaymentRequestEvent {
    private final Order order;

    public PaymentRequestEvent(Order order) {
        this.order = order;
    }

}

