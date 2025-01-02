package com.comp5348.storeapp.service;

import com.comp5348.storeapp.event.EmailServiceRequestEvent;
import com.comp5348.storeapp.message.MessageSender;
import com.comp5348.storeapp.model.Order;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.context.ApplicationEventPublisher;

@Log
@Service
public class EmailService {

    //    private final MessageSender messageSender;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public EmailService(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
//        this.messageSender = messageSender;
    }

    public void sendEmail(String emailType, Order order) {
        log.info("Sending email of type " + emailType + " for order ID: " + order.getId());
//        messageSender.sendEmailRequest(emailType, order);
        eventPublisher.publishEvent(new EmailServiceRequestEvent(emailType, order));
    }
}
