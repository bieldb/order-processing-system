package com.gabrielalves.order_processing_system.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.BatchSize;

import com.gabrielalves.order_processing_system.exception.InvalidOrderStatusException;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    // Em listagens paginadas, carrega os itens de vários pedidos em uma única consulta (evita N+1 sem fetch join + paginação em memória).
    @BatchSize(size = 50)
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OrderStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "total", nullable = false, precision = 19, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    public Order(Customer customer) {
        this.customer = customer;
        this.status = OrderStatus.CREATED;
    }

    public void addItem(OrderItem item) {
        item.setOrder(this);
        this.items.add(item);
        this.total = total.add(item.getSubtotal());
    }

    public List<OrderItem> getItems() {
        return List.copyOf(items);
    }

    public void process() {
        transitionTo(OrderStatus.PROCESSING);
    }

    public void complete() {
        transitionTo(OrderStatus.COMPLETED);
    }

    // O estoque é reservado na criação do pedido, então o cancelamento devolve as unidades.
    public void cancel() {
        transitionTo(OrderStatus.CANCELLED);
        items.forEach(item -> item.getProduct().increaseStock(item.getQuantity()));
    }

    private void transitionTo(OrderStatus next) {
        if (!status.canTransitionTo(next)) {
            throw new InvalidOrderStatusException(status, next);
        }
        this.status = next;
    }

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
