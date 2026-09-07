package br.com.orderprocessing.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import br.com.orderprocessing.domain.Customer;
import br.com.orderprocessing.domain.Order;
import br.com.orderprocessing.domain.OrderItem;
import br.com.orderprocessing.exception.OrderNotFoundException;

public class OrderService {

    private final Map<UUID, Order> orders = new HashMap<>();

    public Order createOrder(Customer customer, List<OrderItem> items) {
        Order order = new Order(UUID.randomUUID(), customer);

        for (OrderItem item : items) {
            order.addItem(item);
        }
        orders.put(order.getId(), order);
        return order;
    }

    public Optional<Order> findOrderById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Order id cannot be null");
        }
        return Optional.ofNullable(orders.get(id));

    }

    public List<Order> findOrdersByCustomer(UUID customerId) {
        if (customerId == null) {
            throw new IllegalArgumentException("Customer id cannot be null");
        }

        return orders.values().stream()
                .filter(order -> customerId.equals(order.getCustomer().getId()))
                .toList();
    }
}
