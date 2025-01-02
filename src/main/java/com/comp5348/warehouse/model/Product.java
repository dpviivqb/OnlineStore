package com.comp5348.warehouse.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
public class Product {
    @Id
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private double price;

    @Column(length = 500)
    private String description;

    @Version
    private int version;

    public Product(String name, double price) {
        this.name = name;
        this.price = price;
    }
}
