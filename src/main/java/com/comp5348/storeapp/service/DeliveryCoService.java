package com.comp5348.storeapp.service;

import com.comp5348.storeapp.event.DeliveryCoRequestEvent;
import com.comp5348.storeapp.message.MessageSender;
import com.comp5348.storeapp.model.DeliveryStatus;
import com.comp5348.storeapp.model.OrderStatus;
import com.comp5348.storeapp.model.Order;
import com.comp5348.storeapp.model.PaymentStatus;
import com.comp5348.storeapp.repository.OrderRepository;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Log
@Service
public class DeliveryCoService {

//    private final MessageSender messageSender;
    private final ApplicationEventPublisher eventPublisher;
    private final OrderRepository orderRepository;

    @Autowired
    public DeliveryCoService(ApplicationEventPublisher eventPublisher, OrderRepository orderRepository) {
        this.eventPublisher = eventPublisher;
//        this.messageSender = messageSender;
        this.orderRepository = orderRepository;
    }

    public void sendDeliveryRequest(Long orderId) {
        log.info("Sending delivery request for order ID: " + orderId);
        eventPublisher.publishEvent(new DeliveryCoRequestEvent(orderId));
//        messageSender.sendDeliveryRequest(orderId, null); // Adjust if needed
    }

    public void updateDeliveryStatus(Long orderId, String deliveryStatus) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            order.setDeliveryStatus(DeliveryStatus.valueOf(deliveryStatus));
            if (order.getDeliveryStatus() == DeliveryStatus.DELIVERED && order.getPaymentStatus() == PaymentStatus.SUCCESS) {
                order.setOrderStatus(OrderStatus.COMPLETED);
            }
            orderRepository.save(order);
            log.info("Updated delivery status for Order ID: " + orderId + " to " + deliveryStatus);
        } else {
            log.warning("Order not found for Order ID: " + orderId);
        }
    }
}
