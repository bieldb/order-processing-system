package com.gabrielalves.order_processing_system.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gabrielalves.order_processing_system.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, UUID id);
}
