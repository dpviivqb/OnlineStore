package com.comp5348.deliveryCo.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Data
@Table(name = "deliveries")
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long orderId;

    @Column(nullable = false)
    private String deliveryAddress;

    @Column(nullable = false)
    private LocalDateTime updateTime;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;

    @Version
    private int version;

    public Delivery(Long orderId, String deliveryAddress) {
        this.orderId = orderId;
        this.deliveryAddress = deliveryAddress;
        this.updateTime = LocalDateTime.now();
    }

}


