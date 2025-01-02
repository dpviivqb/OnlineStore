package com.comp5348.storeapp.repository;


import com.comp5348.storeapp.model.Order;
import com.comp5348.storeapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
public interface OrderRepository extends JpaRepository<Order, Long>{

    Optional<Order> findByUserId(Long userId);
}
