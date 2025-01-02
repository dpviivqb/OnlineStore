package com.comp5348.storeapp.controller;

import com.comp5348.storeapp.event.EmailServiceRequestEvent;
import com.comp5348.storeapp.model.Order;
import com.comp5348.storeapp.service.EmailService;
import com.comp5348.storeapp.repository.OrderRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

@RestController
@RequestMapping("/email")
public class EmailServiceController {

//    private final EmailService emailService;
    private final ApplicationEventPublisher eventPublisher;
    private final OrderRepository orderRepository;

    public EmailServiceController(ApplicationEventPublisher eventPublisher, OrderRepository orderRepository) {
        this.eventPublisher = eventPublisher;
//        this.emailService = emailService;
        this.orderRepository = orderRepository;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@RequestBody EmailRequest request) {
        Optional<Order> orderOpt = orderRepository.findById(request.orderId);
        if (orderOpt.isPresent()) {
//            emailService.sendEmail(request.emailType, orderOpt.get());
            eventPublisher.publishEvent(new EmailServiceRequestEvent(request.emailType, orderOpt.get()));
            return ResponseEntity.ok("Email of type " + request.emailType +
                    " sent for order ID: " + request.orderId);
        } else {
            return ResponseEntity.status(404).body("Order not found: " + request.orderId);
        }
    }

    public static class EmailRequest{
        public String emailType;
        public Long orderId;
    }
}
