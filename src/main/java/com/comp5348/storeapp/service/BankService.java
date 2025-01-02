package com.comp5348.storeapp.service;

import com.comp5348.storeapp.event.PaymentRequestEvent;
import com.comp5348.storeapp.event.RefundRequestEvent;
import com.comp5348.storeapp.model.Order;
import com.comp5348.storeapp.repository.OrderRepository;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Log
@Service
public class BankService {

//    private final MessageSender messageSender;

    private final ApplicationEventPublisher eventPublisher;
    private final OrderRepository orderRepository;

    @Autowired
    public BankService(ApplicationEventPublisher eventPublisher, OrderRepository orderRepository) {
        this.eventPublisher = eventPublisher;
//        this.messageSender = messageSender;
        this.orderRepository = orderRepository;
    }

    public void sendPaymentRequest(Long orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            log.info("Publishing PaymentEvent for order ID: " + orderId);
            eventPublisher.publishEvent(new PaymentRequestEvent(order));
//            log.info("Sending payment request for order ID: " + order.getId());
//            messageSender.sendPaymentRequest(order);
        } else {
            log.warning("Order not found for payment request, ID: " + orderId);
        }
    }

    public void sendRefundRequest(Long orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            log.info("Publishing RefundEvent for order ID: " + orderId);
            eventPublisher.publishEvent(new RefundRequestEvent(order));
//            log.info("Sending refund request for order ID: " + order.getId());
//            messageSender.sendRefundRequest(order);
        } else {
            log.warning("Order not found for refund request, ID: " + orderId);
        }
    }
}
