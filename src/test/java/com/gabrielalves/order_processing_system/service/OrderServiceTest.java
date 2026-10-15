package com.gabrielalves.order_processing_system.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import com.gabrielalves.order_processing_system.dto.OrderItemRequestDTO;
import com.gabrielalves.order_processing_system.dto.OrderRequestDTO;
import com.gabrielalves.order_processing_system.dto.OrderResponseDTO;
import com.gabrielalves.order_processing_system.entity.Customer;
import com.gabrielalves.order_processing_system.entity.Order;
import com.gabrielalves.order_processing_system.entity.OrderItem;
import com.gabrielalves.order_processing_system.entity.OrderStatus;
import com.gabrielalves.order_processing_system.entity.Product;
import com.gabrielalves.order_processing_system.exception.CustomerNotFoundException;
import com.gabrielalves.order_processing_system.exception.InsufficientStockException;
import com.gabrielalves.order_processing_system.exception.InvalidOrderStatusException;
import com.gabrielalves.order_processing_system.exception.OrderNotFoundException;
import com.gabrielalves.order_processing_system.exception.ProductNotFoundException;
import com.gabrielalves.order_processing_system.mapper.OrderMapper;
import com.gabrielalves.order_processing_system.repository.CustomerRepository;
import com.gabrielalves.order_processing_system.repository.OrderRepository;
import com.gabrielalves.order_processing_system.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProductRepository productRepository;

    @Spy
    private OrderMapper orderMapper = Mappers.getMapper(OrderMapper.class);

    @InjectMocks
    private OrderService orderService;

    private final UUID customerId = UUID.randomUUID();
    private final UUID orderId = UUID.randomUUID();
    private final Customer customer = customer();
    private final Product keyboard = product("Teclado", "199.90", 10);
    private final Product mouse = product("Mouse", "49.95", 5);

    @Test
    void shouldCreateOrderCalculatingTotalAndReservingStock() {
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(productRepository.findAllById(Set.of(keyboard.getId(), mouse.getId())))
                .thenReturn(List.of(keyboard, mouse));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> withId(invocation.getArgument(0)));

        OrderResponseDTO response = orderService.create(request(item(keyboard, 2), item(mouse, 1)));

        assertThat(response.id()).isEqualTo(orderId);
        assertThat(response.customerId()).isEqualTo(customerId);
        assertThat(response.status()).isEqualTo(OrderStatus.CREATED);
        assertThat(response.total()).isEqualByComparingTo("449.75");
        assertThat(response.items()).hasSize(2);
        assertThat(keyboard.getStock()).isEqualTo(8);
        assertThat(mouse.getStock()).isEqualTo(4);
    }

    @Test
    void shouldMergeRepeatedProductsIntoSingleItem() {
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(productRepository.findAllById(Set.of(keyboard.getId()))).thenReturn(List.of(keyboard));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> withId(invocation.getArgument(0)));

        OrderResponseDTO response = orderService.create(request(item(keyboard, 2), item(keyboard, 3)));

        assertThat(response.items()).singleElement()
                .satisfies(item -> assertThat(item.quantity()).isEqualTo(5));
        assertThat(response.total()).isEqualByComparingTo("999.50");
        assertThat(keyboard.getStock()).isEqualTo(5);
    }

    @Test
    void shouldNotCreateOrderForNonexistentCustomer() {
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.create(request(item(keyboard, 1))))
                .isInstanceOf(CustomerNotFoundException.class);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void shouldNotCreateOrderWithNonexistentProduct() {
        UUID missing = UUID.randomUUID();
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(productRepository.findAllById(Set.of(keyboard.getId(), missing))).thenReturn(List.of(keyboard));

        assertThatThrownBy(() -> orderService.create(request(item(keyboard, 1), new OrderItemRequestDTO(missing, 1))))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining(missing.toString());
        verify(orderRepository, never()).save(any());
        assertThat(keyboard.getStock()).isEqualTo(10);
    }

    @Test
    void shouldNotCreateOrderWithInsufficientStock() {
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(productRepository.findAllById(Set.of(mouse.getId()))).thenReturn(List.of(mouse));

        assertThatThrownBy(() -> orderService.create(request(item(mouse, 6))))
                .isInstanceOf(InsufficientStockException.class)
                .hasMessageContaining("solicitado 6, disponível 5");
        verify(orderRepository, never()).save(any());
    }

    @Test
    void shouldFindOrderById() {
        when(orderRepository.findWithItemsById(orderId)).thenReturn(Optional.of(order(item(keyboard, 1))));

        assertThat(orderService.findById(orderId).id()).isEqualTo(orderId);
    }

    @Test
    void shouldThrowWhenOrderDoesNotExist() {
        when(orderRepository.findWithItemsById(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.findById(orderId))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessageContaining(orderId.toString());
    }

    @Test
    void shouldListAllOrdersWhenStatusIsNotInformed() {
        PageRequest pageable = PageRequest.of(0, 10);
        when(orderRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(order()), pageable, 1));

        assertThat(orderService.findAll(null, pageable).getTotalElements()).isEqualTo(1);
        verify(orderRepository, never()).findByStatus(any(), any());
    }

    @Test
    void shouldFilterOrdersByStatus() {
        PageRequest pageable = PageRequest.of(0, 10);
        when(orderRepository.findByStatus(OrderStatus.CREATED, pageable))
                .thenReturn(new PageImpl<>(List.of(order()), pageable, 1));

        assertThat(orderService.findAll(OrderStatus.CREATED, pageable).getContent())
                .extracting(OrderResponseDTO::status)
                .containsExactly(OrderStatus.CREATED);
    }

    @Test
    void shouldListOrdersByCustomer() {
        PageRequest pageable = PageRequest.of(0, 10);
        when(customerRepository.existsById(customerId)).thenReturn(true);
        when(orderRepository.findByCustomerId(customerId, pageable))
                .thenReturn(new PageImpl<>(List.of(order()), pageable, 1));

        assertThat(orderService.findByCustomer(customerId, pageable).getContent())
                .extracting(OrderResponseDTO::customerId)
                .containsExactly(customerId);
    }

    @Test
    void shouldThrowWhenListingOrdersOfNonexistentCustomer() {
        when(customerRepository.existsById(customerId)).thenReturn(false);

        assertThatThrownBy(() -> orderService.findByCustomer(customerId, PageRequest.of(0, 10)))
                .isInstanceOf(CustomerNotFoundException.class);
    }

    @Test
    void shouldProcessCreatedOrder() {
        when(orderRepository.findWithItemsById(orderId)).thenReturn(Optional.of(order()));

        assertThat(orderService.process(orderId).status()).isEqualTo(OrderStatus.PROCESSING);
    }

    @Test
    void shouldCompleteProcessingOrder() {
        Order order = order();
        order.process();
        when(orderRepository.findWithItemsById(orderId)).thenReturn(Optional.of(order));

        assertThat(orderService.complete(orderId).status()).isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    void shouldCancelOrderAndReturnStock() {
        keyboard.decreaseStock(3);
        when(orderRepository.findWithItemsById(orderId)).thenReturn(Optional.of(order(item(keyboard, 3))));

        assertThat(orderService.cancel(orderId).status()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(keyboard.getStock()).isEqualTo(10);
    }

    @Test
    void shouldNotProcessCancelledOrder() {
        Order order = order();
        order.cancel();
        when(orderRepository.findWithItemsById(orderId)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.process(orderId))
                .isInstanceOf(InvalidOrderStatusException.class);
    }

    @Test
    void shouldNotCancelCompletedOrder() {
        Order order = order(item(keyboard, 1));
        order.process();
        order.complete();
        when(orderRepository.findWithItemsById(orderId)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.cancel(orderId))
                .isInstanceOf(InvalidOrderStatusException.class);
        assertThat(keyboard.getStock()).isEqualTo(10);
    }

    @Test
    void shouldThrowWhenChangingStatusOfNonexistentOrder() {
        when(orderRepository.findWithItemsById(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.cancel(orderId))
                .isInstanceOf(OrderNotFoundException.class);
    }

    private OrderRequestDTO request(OrderItemRequestDTO... items) {
        return new OrderRequestDTO(customerId, List.of(items));
    }

    private static OrderItemRequestDTO item(Product product, int quantity) {
        return new OrderItemRequestDTO(product.getId(), quantity);
    }

    private Order order(OrderItemRequestDTO... items) {
        Order order = new Order(customer);
        for (OrderItemRequestDTO item : items) {
            Product product = item.productId().equals(keyboard.getId()) ? keyboard : mouse;
            order.addItem(new OrderItem(product, item.quantity()));
        }
        return withId(order);
    }

    private Order withId(Order order) {
        ReflectionTestUtils.setField(order, "id", orderId);
        return order;
    }

    private Customer customer() {
        Customer customer = new Customer();
        ReflectionTestUtils.setField(customer, "id", customerId);
        return customer;
    }

    private static Product product(String name, String price, int stock) {
        Product product = new Product(name, new BigDecimal(price), stock);
        ReflectionTestUtils.setField(product, "id", UUID.randomUUID());
        return product;
    }
}
