package com.comp5348.storeapp.service;

import com.comp5348.storeapp.event.WarehouseStockCheckRequestEvent;
import com.comp5348.storeapp.event.WarehouseStockReleaseRequestEvent;
import com.comp5348.storeapp.message.MessageSender;
import com.comp5348.storeapp.model.Order;
import com.comp5348.storeapp.repository.OrderRepository;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.context.ApplicationEventPublisher;

@Log
@Service
public class WarehouseService {

//    private final MessageSender messageSender;

    private final ApplicationEventPublisher eventPublisher;
//    private final OrderRepository orderRepository;

    @Autowired
    public WarehouseService(ApplicationEventPublisher eventPublisher) {
//        this.messageSender = messageSender;
//        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
    }

    public void checkStock(Long orderId, Long productId, int quantity) {
        log.info("Sending warehouse stock check for order ID: " + orderId);
//        messageSender.sendWarehouseCheck(orderId, productId, quantity);
        eventPublisher.publishEvent(new WarehouseStockCheckRequestEvent(orderId, productId, quantity));
    }

    public void releaseStock(Long orderId) {
        log.info("Sending warehouse release stock request for order ID: " + orderId);
//        messageSender.sendWarehouseReleaseRequest(orderId);
        eventPublisher.publishEvent(new WarehouseStockReleaseRequestEvent(orderId));
    }
}
