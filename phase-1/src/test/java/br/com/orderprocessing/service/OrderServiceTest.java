package br.com.orderprocessing.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import br.com.orderprocessing.domain.Customer;
import br.com.orderprocessing.domain.Order;
import br.com.orderprocessing.domain.OrderItem;
import br.com.orderprocessing.domain.OrderStatus;
import br.com.orderprocessing.domain.Product;

public class OrderServiceTest {

    public static void main(String[] args) {
        shouldCreateOrderWithCreatedStatusAndItems();
        shouldFindOrderByIdWhenOrderExists();
        shouldFindOrdersByCustomer();

        System.out.println("All OrderService tests passed.");
    }

    private static void shouldCreateOrderWithCreatedStatusAndItems() {
        OrderService orderService = new OrderService();
        Customer customer = customer("Gabriel");
        OrderItem item = new OrderItem(product("10.00"), 2);

        Order order = orderService.createOrder(customer, List.of(item));

        assertNotNull(order.getId());
        assertEquals(OrderStatus.CREATED, order.getStatus());
        assertEquals(customer.getId(), order.getCustomer().getId());
        assertEquals(1, order.getItems().size());
    }

    private static void shouldFindOrderByIdWhenOrderExists() {
        OrderService orderService = new OrderService();
        Order createdOrder = orderService.createOrder(customer("Gabriel"), List.of());

        Order foundOrder = orderService.findOrderById(createdOrder.getId())
            .orElseThrow(() -> new AssertionError("Expected order to be found"));

        assertEquals(createdOrder.getId(), foundOrder.getId());
    }

    private static void shouldFindOrdersByCustomer() {
        OrderService orderService = new OrderService();
        Customer firstCustomer = customer("Gabriel");
        Customer secondCustomer = customer("Ana");

        Order firstOrder = orderService.createOrder(firstCustomer, List.of());
        Order secondOrder = orderService.createOrder(firstCustomer, List.of());
        orderService.createOrder(secondCustomer, List.of());

        List<Order> customerOrders = orderService.findOrdersByCustomer(firstCustomer.getId());

        assertEquals(2, customerOrders.size());
        assertTrue(customerOrders.stream()
            .anyMatch(order -> order.getId().equals(firstOrder.getId())));
        assertTrue(customerOrders.stream()
            .anyMatch(order -> order.getId().equals(secondOrder.getId())));
    }

    private static Customer customer(String name) {
        return new Customer(
            UUID.randomUUID(),
            name,
            name.toLowerCase() + "@email.com"
        );
    }

    private static Product product(String price) {
        return new Product(
            UUID.randomUUID(),
            "Notebook",
            new BigDecimal(price),
            10
        );
    }

    private static void assertNotNull(Object value) {
        if (value == null) {
            throw new AssertionError("Expected value not to be null");
        }
    }

    private static void assertEquals(Object expected, Object actual) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError("Expected " + expected + ", but was " + actual);
        }
    }

    private static void assertTrue(boolean condition) {
        if (!condition) {
            throw new AssertionError("Expected condition to be true");
        }
    }
}
