package com.comp5348.bank.message;


import com.comp5348.bank.config.RabbitMQConfig;
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

    public void sendPaymentResponse(Long orderId, boolean isSuccess) {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("orderId", orderId);
            response.put("isSuccess", isSuccess);

            String jsonMessage = objectMapper.writeValueAsString(response);

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.ORDER_RESPONSE_EXCHANGE,
                    RabbitMQConfig.PAYMENT_RESPONSE_ROUTING_KEY,
                    jsonMessage
            );

            log.info("Sent payment response for orderId=" + orderId);
        } catch (Exception e) {
            log.severe("Failed to send payment response: " + e.getMessage());
        }
    }

    public void sendRefundResponse(Long orderId, boolean isSuccess) {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("orderId", orderId);
            response.put("isSuccess", isSuccess);

            String jsonMessage = objectMapper.writeValueAsString(response);

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.ORDER_RESPONSE_EXCHANGE,
                    RabbitMQConfig.REFUND_RESPONSE_ROUTING_KEY,
                    jsonMessage
            );

            log.info("Sent refund response for orderId=" + orderId);
        } catch (Exception e) {
            log.severe("Failed to send refund response: " + e.getMessage());
        }
    }
}
