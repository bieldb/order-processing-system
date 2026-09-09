package com.order_processing_system.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.order_processing_system.entity.Product;

public interface ProductRepository extends JpaRepository<Product, UUID> {

}
