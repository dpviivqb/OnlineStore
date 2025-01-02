package com.comp5348.warehouse.message;

import com.comp5348.warehouse.config.RabbitMQConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MessageSender {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public MessageSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = new ObjectMapper();
    }

    public void sendWarehouseResponse(Long orderId, boolean isAvailable) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("orderId", orderId);
            payload.put("isAvailable", isAvailable);

            String jsonMessage = objectMapper.writeValueAsString(payload);

            rabbitTemplate.convertAndSend(RabbitMQConfig.ORDER_RESPONSE_EXCHANGE,
                    RabbitMQConfig.WAREHOUSE_RESPONSE_ROUTING_KEY, jsonMessage);
        } catch (Exception e) {
            System.err.println("Failed to send warehouse response: " + e.getMessage());
        }
    }
}
