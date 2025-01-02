package com.comp5348.storeapp.message;

import com.comp5348.storeapp.config.RabbitMQConfig;
import com.comp5348.storeapp.config.StoreBankConfig;
import com.comp5348.storeapp.event.*;
import com.comp5348.storeapp.model.*;
import com.comp5348.storeapp.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.java.Log;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.comp5348.storeapp.repository.UserRepository;
import java.util.HashMap;
import java.util.Map;

@Log
@Component
public class MessageSender {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final StoreBankConfig storeBankConfig;


    public MessageSender(RabbitTemplate rabbitTemplate, UserRepository userRepository, OrderRepository orderRepository, StoreBankConfig storeBankConfig) {
        this.rabbitTemplate = rabbitTemplate;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.storeBankConfig = storeBankConfig;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Send warehouse check request
     */
    @EventListener
    public void sendWarehouseCheck(WarehouseStockCheckRequestEvent event){
        Long orderId = event.getOrderId();
        Long productId = event.getProductId();
        int quantity = event. getQuantity();

        try {
            log.info("Preparing to send warehouse check request for order ID: " + orderId);

            Map<String, Object> payload = new HashMap<>();
            payload.put("orderId", orderId);
            payload.put("productId", productId);
            payload.put("quantity", quantity);

            String jsonMessage = objectMapper.writeValueAsString(payload);
            log.info("Warehouse check payload: " + jsonMessage);

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.ORDER_REQUEST_EXCHANGE,
                    RabbitMQConfig.WAREHOUSE_REQUEST_ROUTING_KEY,
                    jsonMessage
            );

            log.info("Warehouse check request sent for order ID: " + orderId);
        } catch (Exception e) {
            log.severe("Failed to send warehouse check message: " + e.getMessage());
        }
    }


    /**
     * Send payment request
     */
    @EventListener
    public void sendPaymentRequest(PaymentRequestEvent event) {
        Order order = event.getOrder();
        Long storeCustomerId = storeBankConfig.getStoreCustomerId();
        Long storeAccountId = storeBankConfig.getStoreAccountId();
        try {
            // 获取用户和银行账户信息
            User user = userRepository.findById(order.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found: userId=" + order.getUserId()));

            if (user.getBankCustomerId() == null || user.getBankAccountId() == null) {
                log.warning("User has not linked a bank account: userId=" + user.getId());

                // 更新订单状态为 "支付失败"，并保存订单状态
                order.setPaymentStatus(PaymentStatus.FAILED);
                order.setOrderStatus(OrderStatus.FAILED);
                orderRepository.save(order);

                log.info("Order marked as FAILED due to missing bank account: orderId=" + order.getId());
                return;
            }

            // 构建支付请求的 payload
            Map<String, Object> payload = new HashMap<>();
            payload.put("orderId", order.getId());
            payload.put("fromCustomerId", storeCustomerId);
            payload.put("fromAccountId", storeAccountId);
            payload.put("toCustomerId", user.getBankCustomerId());
            payload.put("toAccountId", user.getBankAccountId());
            payload.put("amount", order.getTotalAmount());
            payload.put("memo", "Payment for Order ID: " + order.getId());

            // 将 payload 转换为 JSON 格式并发送消息到 RabbitMQ
            String jsonMessage = objectMapper.writeValueAsString(payload);

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.ORDER_REQUEST_EXCHANGE,
                    RabbitMQConfig.PAYMENT_REQUEST_ROUTING_KEY,
                    jsonMessage
            );

            log.info("Payment request sent for Order ID: " + order.getId());

        } catch (Exception e) {
            // 捕获异常，记录错误日志，并标记订单为失败状态
            log.severe("Failed to send payment request for Order ID: " + order.getId() + ". Error: " + e.getMessage());

            // 更新订单状态为失败，并保存更改
            order.setPaymentStatus(PaymentStatus.FAILED);
            order.setOrderStatus(OrderStatus.FAILED);
            orderRepository.save(order);
        }
    }


    /**
     * Notify delivery company to ship the order (not yet implemented)
     */
    @EventListener
    public void sendDeliveryRequest(DeliveryCoRequestEvent event) {
        Long orderId = event.getOrderId();
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("orderId", orderId);

            String jsonMessage = objectMapper.writeValueAsString(payload);

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.ORDER_REQUEST_EXCHANGE,
                    RabbitMQConfig.DELIVERY_REQUEST_ROUTING_KEY,
                    jsonMessage
            );

            log.info("Delivery request sent for Order ID: " + orderId);

        } catch (Exception e) {
            log.severe("Failed to send delivery request message: " + e.getMessage());
        }
    }

    /**
     * Send a refund request
     */
    @EventListener
    public void sendRefundRequest(RefundRequestEvent event) {
        Order order = event.getOrder();
        // 获取商店的银行账户信息
        Long storeCustomerId = storeBankConfig.getStoreCustomerId();
        Long storeAccountId = storeBankConfig.getStoreAccountId();
        try {
            // 获取用户的银行账户信息
            User user = userRepository.findById(order.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            if (user.getBankCustomerId() == null || user.getBankAccountId() == null) {
                log.warning("User has not linked a bank account: userId=" + user.getId());
                return;
            }

            Map<String, Object> payload = new HashMap<>();
            payload.put("orderId", order.getId());
            payload.put("fromCustomerId", storeCustomerId);
            payload.put("fromAccountId", storeAccountId);
            payload.put("toCustomerId", user.getBankCustomerId());
            payload.put("toAccountId", user.getBankAccountId());
            payload.put("amount", order.getTotalAmount());
            payload.put("memo", "Refund for Order ID: " + order.getId());

            String jsonMessage = objectMapper.writeValueAsString(payload);

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.ORDER_REQUEST_EXCHANGE,
                    RabbitMQConfig.REFUND_REQUEST_ROUTING_KEY,
                    jsonMessage
            );

            log.info("Refund request sent for Order ID: " + order.getId());

        } catch (Exception e) {
            log.severe("Failed to send refund request: " + e.getMessage());
        }
    }

    /**
     * Send inventory update request
     */
    @EventListener
    public void sendWarehouseReleaseRequest(WarehouseStockReleaseRequestEvent event) {
        Long orderId = event.getOrderId();
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("orderId", orderId);

            String jsonMessage = objectMapper.writeValueAsString(payload);

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.ORDER_REQUEST_EXCHANGE,
                    RabbitMQConfig.WAREHOUSE_UPDATE_ROUTING_KEY,
                    jsonMessage
            );

            log.info("Warehouse release request sent for Order ID: " + orderId);

        } catch (Exception e) {
            log.severe("Failed to send warehouse release request: " + e.getMessage());
        }
    }

    /**
     * Notify email service to send an email (not yet implemented)
     */
    @EventListener
    public void sendEmailRequest(EmailServiceRequestEvent event) {
        String emailType = event.getEmailType();
        Order order = event.getOrder();
        try {
            // Retrieve user information
            User user = userRepository.findById(order.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            // Initialize email subject and content
            String subject;
            String content;

            switch (emailType) {
                case "OUT_OF_STOCK":
                    subject = "Your Order #" + order.getId() + " Could Not Be Processed";
                    content = "Dear " + user.getUsername() + ",\n\n"
                            + "We regret to inform you that your order #" + order.getId()
                            + " could not be processed due to insufficient stock.\n"
                            + "Please contact our customer support for further assistance.\n\n"
                            + "Best regards,\n"
                            + "Shoppingo Team";

                    log.info("Preparing OUT_OF_STOCK email, Order ID: " + order.getId());
                    break;

                case "PAYMENT_FAILED":
                    subject = "Payment Failed for Order #" + order.getId();
                    content = "Dear " + user.getUsername() + ",\n\n"
                            + "Unfortunately, the payment for your order #" + order.getId()
                            + " failed.\n"
                            + "Please check your payment details or contact our customer support.\n\n"
                            + "Best regards,\n"
                            + "Shoppingo Team";

                    log.info("Preparing PAYMENT_FAILED email, Order ID: " + order.getId());
                    break;

                case "ORDER_CANCELLED":
                    subject = "Your Order #" + order.getId() + " Has Been Cancelled";
                    content = "Dear " + user.getUsername() + ",\n\n"
                            + "Your order #" + order.getId() + " has been successfully cancelled.\n"
                            + "A refund of $" + order.getTotalAmount() + " will be processed shortly.\n\n"
                            + "Best regards,\n"
                            + "Shoppingo Team";

                    log.info("Preparing ORDER_CANCELLED email, Order ID: " + order.getId());
                    break;

                case "REFUND_SUCCESS":
                    subject = "Refund Successful for Order #" + order.getId();
                    content = "Dear " + user.getUsername() + ",\n\n"
                            + "The refund for your order #" + order.getId() + " has been processed successfully.\n"
                            + "An amount of $" + order.getTotalAmount() + " has been credited to your account.\n\n"
                            + "Best regards,\n"
                            + "Shoppingo Team";

                    log.info("Preparing REFUND_SUCCESS email, Order ID: " + order.getId());
                    break;

                case "DELIVERY_STATUS_UPDATE":
                    // Handle delivery status updates
                    DeliveryStatus deliveryStatus = order.getDeliveryStatus();
                    subject = "Delivery Status Update for Order #" + order.getId();
                    content = "Dear " + user.getUsername() + ",\n\n"
                            + "The current status of your order #" + order.getId() + " is: " + deliveryStatus.toString() + ".\n";

                    if (deliveryStatus == DeliveryStatus.PICKED_UP) {
                        content += "Your item has been picked up by the delivery company and is on its way to the warehouse.\n";
                        log.info("Preparing DELIVERY_STATUS_UPDATE email (PICKED_UP), Order ID: " + order.getId());
                    } else if (deliveryStatus == DeliveryStatus.IN_TRANSIT) {
                        content += "Your item is on the way and will arrive soon.\n";
                        log.info("Preparing DELIVERY_STATUS_UPDATE email (IN_TRANSIT), Order ID: " + order.getId());
                    } else if (deliveryStatus == DeliveryStatus.DELIVERED) {
                        content += "Your item has been delivered. We hope you are satisfied with our service!\n";
                        log.info("Preparing DELIVERY_STATUS_UPDATE email (DELIVERED), Order ID: " + order.getId());
                    } else {
                        content += "Please log in to your account for more details.\n";
                        log.info("Preparing DELIVERY_STATUS_UPDATE email (" + deliveryStatus + "), Order ID: " + order.getId());
                    }

                    content += "\nBest regards,\nShoppingo Team";
                    break;

                default:
                    subject = "Order #" + order.getId() + " Status Update";
                    content = "Dear " + user.getUsername() + ",\n\n"
                            + "There is a new update regarding your order #" + order.getId() + ".\n"
                            + "Please log in to your account to check the details.\n\n"
                            + "Best regards,\n"
                            + "Shoppingo Team";

                    break;
            }

            Map<String, Object> payload = new HashMap<>();
            payload.put("userId", user.getId());
            payload.put("username", user.getUsername());
            payload.put("subject", subject);
            payload.put("content", content);

            String jsonMessage = objectMapper.writeValueAsString(payload);

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EMAIL_REQUEST_EXCHANGE,
                    RabbitMQConfig.EMAIL_REQUEST_ROUTING_KEY,
                    jsonMessage
            );

            log.info("Email request sent, Order ID: " + order.getId() + ", Email Type: " + emailType);

        } catch (Exception e) {
            log.severe("Failed to send email request, Order ID: " + order.getId() + ", Error: " + e.getMessage());
        }
    }
}
