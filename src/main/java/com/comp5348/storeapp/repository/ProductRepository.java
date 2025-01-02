package com.comp5348.storeapp.repository;


import com.comp5348.storeapp.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long>{

}
