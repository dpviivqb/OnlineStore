package com.comp5348.warehouse.message;

import com.comp5348.warehouse.config.RabbitMQConfig;
import com.comp5348.warehouse.service.WarehouseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.java.Log;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Log
@Component
public class MessageListener {

    private final WarehouseService warehouseService;
    private final MessageSender messageSender;
    private final ObjectMapper objectMapper;

    public MessageListener(WarehouseService warehouseService, MessageSender messageSender) {
        this.warehouseService = warehouseService;
        this.messageSender = messageSender;
        this.objectMapper = new ObjectMapper();
    }

    @RabbitListener(queues = RabbitMQConfig.WAREHOUSE_REQUEST_QUEUE)
    public void handleWarehouseRequest(String message) {
        try {
            Map<String, Object> request = objectMapper.readValue(message, Map.class);
            Long orderId = Long.valueOf(request.get("orderId").toString());
            Long productId = Long.valueOf(request.get("productId").toString());
            int quantity = Integer.parseInt(request.get("quantity").toString());

            boolean isAvailable = warehouseService.isStockAvailable(productId, quantity);

            // Send response back to StoreApp
            messageSender.sendWarehouseResponse(orderId, isAvailable);

            // If stock is available, allocate it
            if (isAvailable) {
                warehouseService.allocateStock(orderId, productId, quantity);
            }

        } catch (Exception e) {
            System.err.println("Failed to handle warehouse request: " + e.getMessage());
            // Optionally, send a failure response if needed
        }
    }

    @RabbitListener(queues = RabbitMQConfig.WAREHOUSE_UPDATE_QUEUE)
    public void handleStockReleaseRequest(String message) {
        try {
            Map<String, Object> request = objectMapper.readValue(message, Map.class);
            Long orderId = Long.valueOf(request.get("orderId").toString());

            log.info("Received stock release request for Order ID: " + orderId);

            // Update stock by releasing the allocated quantity
            warehouseService.releaseStock(orderId);

            log.info("Stock released for Order ID: " + orderId);

        } catch (Exception e) {
            log.severe("Failed to handle stock release request: " + e.getMessage());
        }
    }
}
