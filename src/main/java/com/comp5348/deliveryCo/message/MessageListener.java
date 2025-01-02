package com.comp5348.deliveryCo.message;

import com.comp5348.deliveryCo.config.RabbitMQConfig;
import com.comp5348.deliveryCo.message.MessageSender;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.java.Log;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.util.Map;

@Log
@Component
public class MessageListener {

    private final MessageSender messageSender;
    private final ObjectMapper objectMapper;

    public MessageListener(MessageSender messageSender) {
        this.messageSender = messageSender;
        this.objectMapper = new ObjectMapper();
    }

    @RabbitListener(queues = RabbitMQConfig.DELIVERY_REQUEST_QUEUE, ackMode = "MANUAL")
    public void handleDeliveryRequest(String message, Channel channel, Message mqMessage) throws IOException {
        try {
            // Simulate a 5% packet loss rate
            if (Math.random() < 0.05) {
                throw new RuntimeException("Simulated delivery failure (5% drop rate)");
            }

            // Parse the message
            Map<String, Object> request = objectMapper.readValue(message, Map.class);
            Long orderId = Long.valueOf(request.get("orderId").toString());

            log.info("Received delivery request for Order ID: " + orderId);

            // Send status update: RECEIVED_REQUEST
            messageSender.sendDeliveryStatusUpdate(orderId, "RECEIVED_REQUEST");

            // Simulate the delivery process
            Thread.sleep(5000); // Wait for 5 seconds

            // Send status update: PICKED_UP
            messageSender.sendDeliveryStatusUpdate(orderId, "PICKED_UP");

            Thread.sleep(5000); // Wait for 5 seconds

            // Send status update: IN_TRANSIT
            messageSender.sendDeliveryStatusUpdate(orderId, "IN_TRANSIT");

            Thread.sleep(5000); // Wait for 5 seconds

            // Send status update: DELIVERED
            messageSender.sendDeliveryStatusUpdate(orderId, "DELIVERED");

            log.info("Delivery completed for Order ID: " + orderId);

            // Send ACK confirmation after successful processing
            channel.basicAck(mqMessage.getMessageProperties().getDeliveryTag(), false);
            log.info("Message ACK sent for Order ID: " + orderId);

        } catch (Exception e) {
            log.severe("Failed to handle delivery request: " + e.getMessage());

            // Reject the message and requeue it for retry upon failure
            channel.basicNack(mqMessage.getMessageProperties().getDeliveryTag(), false, true);
        }
    }
}
