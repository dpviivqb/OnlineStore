package com.comp5348.storeapp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@NoArgsConstructor
@Data
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private double totalAmount;

    @Column(nullable = false)
    private LocalDateTime orderTime;

    @Enumerated(value = EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus deliveryStatus;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Version
    private int version;

    public Order(Long productId, int quantity, double totalAmount, OrderStatus orderStatus, DeliveryStatus deliveryStatus, PaymentStatus paymentStatus, Long userId) {
        this.productId = productId;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
        this.orderTime = LocalDateTime.now();
        this.orderStatus = orderStatus;
        this.deliveryStatus = deliveryStatus;
        this.paymentStatus = paymentStatus;
        this.userId = userId;
    }
}
