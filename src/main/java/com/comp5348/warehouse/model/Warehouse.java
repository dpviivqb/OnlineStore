package com.comp5348.warehouse.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "warehouses")
public class Warehouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Version
    private int version;

    // 一个仓库（Warehouse）可以关联多个 WarehouseProduct 对象。
    //每个 WarehouseProduct 对象代表一种产品及其数量。
    @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<WarehouseProduct> products;
}
