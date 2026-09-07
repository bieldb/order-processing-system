package br.com.orderprocessing.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import br.com.orderprocessing.exception.InvalidOrderStatusException;

public class Order {
    private UUID id;
    private Customer customer;
    private List<OrderItem> items;
    private OrderStatus status;
    private LocalDateTime createdAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<OrderItem> getItems() {
        return List.copyOf(items);
    }


    public OrderStatus getStatus() {
    return status;
}

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Order(UUID id, Customer customer) {
        this.id = id;
        this.customer = customer;
        this.items = new ArrayList<>();
        this.status = OrderStatus.CREATED;
        this.createdAt = LocalDateTime.now();
    }

    public Order() {
    }

    public void addItem(OrderItem item) {

        if (item == null) {
            throw new IllegalArgumentException("Order item cannot be null");
        }

        items.add(item);
    }

    public void process() {
        if (status != OrderStatus.CREATED) {
            throw new InvalidOrderStatusException("Only CREATED orders can be processed");
        }
        status = OrderStatus.PROCESSING;
    }

    public void complete() {
        if (status != OrderStatus.PROCESSING) {
            throw new InvalidOrderStatusException("Only PROCESSING orders can be completed");
        }
        status = OrderStatus.COMPLETED;
    }

    public void cancel() {
        if (status != OrderStatus.CREATED && status != OrderStatus.PROCESSING) {
            throw new InvalidOrderStatusException("Order cannot be cancelled");
        }
        status = OrderStatus.CANCELLED;
    }

    public BigDecimal getTotal() {
        return items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
