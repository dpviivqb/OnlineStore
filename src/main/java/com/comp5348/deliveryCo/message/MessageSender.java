package com.comp5348.deliveryCo.message;


import com.comp5348.deliveryCo.config.RabbitMQConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.java.Log;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Log
@Component
public class MessageSender {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public MessageSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = new ObjectMapper();
    }

    public void sendDeliveryStatusUpdate(Long orderId, String deliveryStatus) {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("orderId", orderId);
            response.put("deliveryStatus", deliveryStatus);

            String jsonMessage = objectMapper.writeValueAsString(response);

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.ORDER_RESPONSE_EXCHANGE,
                    RabbitMQConfig.DELIVERY_RESPONSE_ROUTING_KEY,
                    jsonMessage
            );

            log.info("Sent delivery status update for Order ID: " + orderId + ", status: " + deliveryStatus);

        } catch (Exception e) {
            log.severe("Failed to send delivery status update: " + e.getMessage());
        }
    }
}

