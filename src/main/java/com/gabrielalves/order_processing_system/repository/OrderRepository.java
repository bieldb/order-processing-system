package com.gabrielalves.order_processing_system.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.gabrielalves.order_processing_system.entity.Order;
import com.gabrielalves.order_processing_system.entity.OrderStatus;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    boolean existsByCustomerId(UUID customerId);

    boolean existsByItemsProductId(UUID productId);

    // Pedido + itens + produtos em um único SELECT: usado na consulta e nas transições (o cancelamento devolve estoque).
    @EntityGraph(attributePaths = { "items", "items.product" })
    Optional<Order> findWithItemsById(UUID id);

    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    Page<Order> findByCustomerId(UUID customerId, Pageable pageable);
}
