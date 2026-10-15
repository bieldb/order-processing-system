package com.gabrielalves.order_processing_system.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.gabrielalves.order_processing_system.exception.InvalidOrderStatusException;

class OrderTest {

    private final Product keyboard = new Product("Teclado", new BigDecimal("199.90"), 10);
    private final Product mouse = new Product("Mouse", new BigDecimal("49.95"), 10);

    @Test
    void shouldStartAsCreatedAndCalculateTotalFromItems() {
        Order order = new Order(new Customer());
        order.addItem(new OrderItem(keyboard, 2));
        order.addItem(new OrderItem(mouse, 3));

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(order.getTotal()).isEqualByComparingTo("549.65");
    }

    @Test
    void shouldFreezeUnitPriceWhenProductPriceChanges() {
        OrderItem item = new OrderItem(keyboard, 2);

        keyboard.update("Teclado", new BigDecimal("999.00"), 10);

        assertThat(item.getUnitPrice()).isEqualByComparingTo("199.90");
        assertThat(item.getSubtotal()).isEqualByComparingTo("399.80");
    }

    @Test
    void shouldProcessAndComplete() {
        Order order = new Order(new Customer());

        order.process();
        order.complete();

        assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    void shouldReturnReservedStockWhenCancelled() {
        Order order = new Order(new Customer());
        keyboard.decreaseStock(2);
        order.addItem(new OrderItem(keyboard, 2));

        order.cancel();

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(keyboard.getStock()).isEqualTo(10);
    }

    @Test
    void shouldNotCancelCompletedOrder() {
        Order order = new Order(new Customer());
        order.addItem(new OrderItem(keyboard, 2));
        order.process();
        order.complete();

        assertThatThrownBy(order::cancel)
                .isInstanceOf(InvalidOrderStatusException.class)
                .hasMessageContaining("COMPLETED -> CANCELLED");
        assertThat(keyboard.getStock()).isEqualTo(10);
    }

    @Test
    void shouldNotProcessCancelledOrder() {
        Order order = new Order(new Customer());
        order.cancel();

        assertThatThrownBy(order::process).isInstanceOf(InvalidOrderStatusException.class);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }
}
