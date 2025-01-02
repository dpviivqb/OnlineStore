package com.comp5348.storeapp.event;

import com.comp5348.storeapp.model.Order;
import lombok.Getter;

@Getter
public class EmailServiceRequestEvent {
    private final String emailType;
    private final Order order;

    public EmailServiceRequestEvent(String emailType, Order order) {
        this.emailType = emailType;
        this.order = order;
    }
}
