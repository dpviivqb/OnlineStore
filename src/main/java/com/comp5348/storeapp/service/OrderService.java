package com.comp5348.storeapp.service;

import com.comp5348.storeapp.message.MessageSender;
import com.comp5348.storeapp.model.*;
import com.comp5348.storeapp.repository.OrderRepository;
import com.comp5348.storeapp.repository.ProductRepository;
import com.comp5348.storeapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

@Log
@Service
public class OrderService {

//    private final ApplicationEventPublisher eventPublisher;
/*    private final MessageSender messageSender;*/
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final WarehouseService warehouseService;
    private final BankService bankService;
//    private final DeliveryCoService deliveryCoService;
    private final EmailService emailService;

    @Autowired
    public OrderService(OrderRepository orderRepository,
                        UserRepository userRepository,
                        ProductRepository productRepository,
                        WarehouseService warehouseService,
                        BankService bankService,
                        EmailService emailService) {
//        this.eventPublisher = eventPublisher;
//        this.messageSender = messageSender;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.warehouseService = warehouseService;
        this.bankService = bankService;
//        this.deliveryCoService = deliveryCoService;
        this.emailService = emailService;
    }

    @Transactional
    public String createOrder(String username, Long productId, Integer quantity) {
        log.info("Creating order for user: " + username + ", product ID: " + productId + ", quantity: " + quantity);

        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            log.warning("User not found: " + username);
            return "User not found.";
        }
        User user = userOpt.get();

        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            log.warning("Product not found: " + productId);
            return "Product not found.";
        }
        Product product = productOpt.get();
        double productPrice = product.getPrice();
        double totalAmount = productPrice * quantity;

        Order order = new Order(productId, quantity, totalAmount, OrderStatus.PENDING,
                DeliveryStatus.PENDING, PaymentStatus.PENDING, user.getId());
        orderRepository.save(order);

        log.info("Order created with ID: " + order.getId());

        // Send message to Warehouse Service to check stock
//        messageSender.sendWarehouseCheck(order.getId(), order.getProductId(), order.getQuantity());
        // Directly call warehouseService to check stock
        warehouseService.checkStock(order.getId(), order.getProductId(), order.getQuantity());

        return "Order created with ID: " + order.getId();
    }

    @Transactional
    public Optional<Order> getOrderById(Long orderId) {
        log.info("Fetching order with ID: " + orderId);
        return orderRepository.findById(orderId);
    }

    @Transactional
    public boolean cancelOrder(Long orderId) {
        log.info("Attempting to cancel order with ID: " + orderId);

        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            if (order.getOrderStatus() == OrderStatus.PENDING) {
                order.setOrderStatus(OrderStatus.CANCELLED);
                orderRepository.save(order);

//                // Notify Warehouse Service to release stock
//                messageSender.sendWarehouseReleaseRequest(orderId);
//
//                // Notify Bank Service to process refund
//                messageSender.sendRefundRequest(order);
//
//                // Notify Email Service to send cancellation email
//                messageSender.sendEmailRequest("ORDER_CANCELLED", order);
                // Directly call warehouseService to release stock
                warehouseService.releaseStock(orderId);

                // Directly call bankService to process refund
                bankService.sendRefundRequest(orderId);

                // Directly call emailService to send cancellation email
                emailService.sendEmail("ORDER_CANCELLED", order);

                log.info("Order cancelled with ID: " + orderId);
                return true;
            } else {
                log.warning("Order cannot be cancelled at this stage: " + orderId);
            }
        } else {
            log.warning("Order not found: " + orderId);
        }
        return false;
    }

}
