package com.comp5348.storeapp.message;

import com.comp5348.storeapp.config.RabbitMQConfig;
import com.comp5348.storeapp.model.*;
import com.comp5348.storeapp.repository.OrderRepository;
import com.comp5348.storeapp.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.java.Log;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;

@Log
@Component
public class MessageListener {

    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper;
    private final BankService bankService;
    private final DeliveryCoService deliveryCoService;
    private final EmailService emailService;
    private final OrderService orderService;

    private final ThreadPoolTaskScheduler taskScheduler;

    public MessageListener(OrderRepository orderRepository,
                           BankService bankService,
                           DeliveryCoService deliveryCoService,
                           EmailService emailService, OrderService orderService, ThreadPoolTaskScheduler taskScheduler) {
        this.orderRepository = orderRepository;
        this.bankService = bankService;
        this.deliveryCoService = deliveryCoService;
        this.emailService = emailService;
        this.orderService = orderService;
        this.taskScheduler = taskScheduler;
        this.objectMapper = new ObjectMapper();
    }

    @RabbitListener(queues = RabbitMQConfig.WAREHOUSE_RESPONSE_QUEUE)
    public void handleWarehouseResponse(String message) {
        try {
            log.info("Received warehouse response: " + message);
            Map<String, Object> response = objectMapper.readValue(message, Map.class);
            Long orderId = Long.valueOf(response.get("orderId").toString());
            boolean isAvailable = Boolean.parseBoolean(response.get("isAvailable").toString());

            Optional<Order> optionalOrder = orderRepository.findById(orderId);
            if (optionalOrder.isPresent()) {
                Order order = optionalOrder.get();

                if (isAvailable) {
                    // 调度任务等待10秒后检查订单状态
                    scheduleOrderProcessing(orderId, order);
                } else {
                    log.warning("Insufficient stock for order ID: " + orderId + ". Marking order as FAILED.");
                    order.setOrderStatus(OrderStatus.FAILED);
                    orderRepository.save(order);

                    emailService.sendEmail("OUT_OF_STOCK", order);
                }
            } else {
                log.warning("Order not found: " + orderId);
            }
        } catch (Exception e) {
            log.severe("Failed to handle warehouse response: " + e.getMessage());
        }
    }
    private void scheduleOrderProcessing(Long orderId, Order order) {
        // 调度任务，10秒后检查订单状态并决定是否继续处理
        taskScheduler.schedule(() -> {
            Optional<Order> optionalOrder = orderRepository.findById(orderId);

            if (optionalOrder.isPresent()) {
                Order currentOrder = optionalOrder.get();

                // 检查订单是否被取消
                if (currentOrder.getOrderStatus() == OrderStatus.CANCELLED) {
                    log.warning("Order ID: " + orderId + " has been cancelled. Skipping further processing.");
                    return; // 订单已取消，停止执行后续步骤
                }

                log.info("Processing order ID: " + orderId);
                currentOrder.setOrderStatus(OrderStatus.PROCESSING);
                orderRepository.save(currentOrder);

                // 发送支付请求
                bankService.sendPaymentRequest(orderId);
            } else {
                log.warning("Order not found: " + orderId);
            }
        }, new Date(System.currentTimeMillis() + 10000)); // 10秒后执行
    }


    /**
     * Handle the response from the payment service.
     */
    @RabbitListener(queues = RabbitMQConfig.PAYMENT_RESPONSE_QUEUE)
    public void handlePaymentResponse(String message) {
        try {
            log.info("Received payment response: " + message);
            Map<String, Object> response = objectMapper.readValue(message, Map.class);
            Long orderId = Long.valueOf(response.get("orderId").toString());
            boolean isSuccess = Boolean.parseBoolean(response.get("isSuccess").toString());

            Optional<Order> optionalOrder = orderRepository.findById(orderId);
            if (optionalOrder.isPresent()) {
                Order order = optionalOrder.get();
                if (isSuccess) {
                    // Payment successful, update payment and order status.
                    log.info("Payment successful for order ID: " + orderId );
                    if (order.getOrderStatus() != OrderStatus.CANCELLED) {
                        // If the order is not cancelled, update status and send delivery request
                        log.info("Order ID: " + orderId + " is not cancelled. Marking as PROCESSING.");
                        order.setPaymentStatus(PaymentStatus.SUCCESS);
                        order.setOrderStatus(OrderStatus.PROCESSING);
                        orderRepository.save(order);

                        // Send delivery request
                        deliveryCoService.sendDeliveryRequest(orderId);
                    } else {
                        log.warning("Order ID: " + orderId + " has been cancelled. Skipping delivery request.");
                    }
                } else {
                    // Payment failed, update order status to FAILED.
                    log.warning("Payment failed for order ID: " + orderId + ". Marking order as FAILED.");
                    order.setPaymentStatus(PaymentStatus.FAILED);
                    order.setOrderStatus(OrderStatus.FAILED);
                    orderRepository.save(order);

                    emailService.sendEmail("PAYMENT_FAILED", order);
                }
            } else {
                log.warning("Order not found: " + orderId);
            }
        } catch (Exception e) {
            log.severe("Failed to handle payment response: " + e.getMessage());
        }
    }

    /**
     * Handle the response from the delivery service.
     */
    @RabbitListener(queues = RabbitMQConfig.DELIVERY_RESPONSE_QUEUE)
    public void handleDeliveryStatusUpdate(String message) {
        try {
            Map<String, Object> response = objectMapper.readValue(message, Map.class);
            Long orderId = Long.valueOf(response.get("orderId").toString());
            String deliveryStatus = response.get("deliveryStatus").toString();

            log.info("Received delivery status update for Order ID: " + orderId + ", status: " + deliveryStatus);

            // Update the delivery status of the order
            deliveryCoService.updateDeliveryStatus(orderId, deliveryStatus);
            Optional<Order> optionalOrder = orderRepository.findById(orderId);
            if (optionalOrder.isPresent()) {
                Order order = optionalOrder.get();
                // 检查订单是否已取消
                if (order.getOrderStatus() == OrderStatus.CANCELLED) {
                    log.warning("Order ID: " + orderId + " has been cancelled. Skipping delivery status update.");
                    return;
                }
                // 更新配送状态
                deliveryCoService.updateDeliveryStatus(orderId, deliveryStatus);
                // Send email notification via EmailService
                if (deliveryStatus.equals("PICKED_UP") || deliveryStatus.equals("IN_TRANSIT") || deliveryStatus.equals("DELIVERED")) {
                    emailService.sendEmail("DELIVERY_STATUS_UPDATE", order);
                }
            } else {
                log.warning("Order not found: " + orderId);
            }

        } catch (Exception e) {
            log.severe("Failed to handle delivery status update: " + e.getMessage());
        }
    }

    /**
     * Handle the response from the refund service.
     */
    @RabbitListener(queues = RabbitMQConfig.REFUND_RESPONSE_QUEUE)
    public void handleRefundResponse(String message) {
        try {
            log.info("Received refund response: " + message);
            Map<String, Object> response = objectMapper.readValue(message, Map.class);
            Long orderId = Long.valueOf(response.get("orderId").toString());
            boolean isSuccess = Boolean.parseBoolean(response.get("isSuccess").toString());

            Optional<Order> optionalOrder = orderRepository.findById(orderId);
            if (optionalOrder.isPresent()) {
                Order order = optionalOrder.get();
                if (isSuccess) {
                    // Refund successful, update order status
                    order.setPaymentStatus(PaymentStatus.REFUNDED);
                    orderRepository.save(order);

                    // Send refund success email notification
                    emailService.sendEmail("REFUND_SUCCESS", order);
                } else {
                    // Refund failed, log or take other actions
                    log.warning("Refund failed for order ID: " + orderId);
                }
            } else {
                log.warning("Order not found: " + orderId);
            }
        } catch (Exception e) {
            log.severe("Failed to handle refund response: " + e.getMessage());
        }
    }
}
