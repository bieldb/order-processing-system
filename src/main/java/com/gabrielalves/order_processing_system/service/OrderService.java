package com.gabrielalves.order_processing_system.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gabrielalves.order_processing_system.dto.OrderItemRequestDTO;
import com.gabrielalves.order_processing_system.dto.OrderRequestDTO;
import com.gabrielalves.order_processing_system.dto.OrderResponseDTO;
import com.gabrielalves.order_processing_system.entity.Customer;
import com.gabrielalves.order_processing_system.entity.Order;
import com.gabrielalves.order_processing_system.entity.OrderItem;
import com.gabrielalves.order_processing_system.entity.OrderStatus;
import com.gabrielalves.order_processing_system.entity.Product;
import com.gabrielalves.order_processing_system.exception.CustomerNotFoundException;
import com.gabrielalves.order_processing_system.exception.OrderNotFoundException;
import com.gabrielalves.order_processing_system.exception.ProductNotFoundException;
import com.gabrielalves.order_processing_system.mapper.OrderMapper;
import com.gabrielalves.order_processing_system.repository.CustomerRepository;
import com.gabrielalves.order_processing_system.repository.OrderRepository;
import com.gabrielalves.order_processing_system.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    /**
     * Cria o pedido e reserva o estoque na mesma transação: se qualquer item falhar
     * (produto inexistente ou estoque insuficiente), nada é persistido.
     */
    @Transactional
    public OrderResponseDTO create(OrderRequestDTO dto) {
        Customer customer = customerRepository.findById(dto.customerId())
                .orElseThrow(() -> new CustomerNotFoundException(dto.customerId()));

        Map<UUID, Integer> quantities = sumQuantitiesByProduct(dto.items());
        Map<UUID, Product> products = findProducts(quantities.keySet());

        Order order = new Order(customer);
        quantities.forEach((productId, quantity) -> {
            Product product = products.get(productId);
            product.decreaseStock(quantity);
            order.addItem(new OrderItem(product, quantity));
        });

        Order saved = orderRepository.save(order);
        log.info("Pedido {} criado para o cliente {} com total {}", saved.getId(), customer.getId(), saved.getTotal());
        return orderMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public OrderResponseDTO findById(UUID id) {
        return orderMapper.toResponse(getOrder(id));
    }

    @Transactional(readOnly = true)
    public Page<OrderResponseDTO> findAll(OrderStatus status, Pageable pageable) {
        Page<Order> orders = status == null
                ? orderRepository.findAll(pageable)
                : orderRepository.findByStatus(status, pageable);
        return orders.map(orderMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponseDTO> findByCustomer(UUID customerId, Pageable pageable) {
        if (!customerRepository.existsById(customerId)) {
            throw new CustomerNotFoundException(customerId);
        }
        return orderRepository.findByCustomerId(customerId, pageable).map(orderMapper::toResponse);
    }

    @Transactional
    public OrderResponseDTO process(UUID id) {
        return changeStatus(id, Order::process);
    }

    @Transactional
    public OrderResponseDTO complete(UUID id) {
        return changeStatus(id, Order::complete);
    }

    @Transactional
    public OrderResponseDTO cancel(UUID id) {
        return changeStatus(id, Order::cancel);
    }

    private OrderResponseDTO changeStatus(UUID id, Consumer<Order> transition) {
        Order order = getOrder(id);
        OrderStatus previous = order.getStatus();
        transition.accept(order);
        log.info("Pedido {} alterado de {} para {}", id, previous, order.getStatus());
        return orderMapper.toResponse(order);
    }

    // Itens repetidos do mesmo produto viram um único item, para validar o estoque pela quantidade total.
    private static Map<UUID, Integer> sumQuantitiesByProduct(List<OrderItemRequestDTO> items) {
        return items.stream().collect(Collectors.toMap(
                OrderItemRequestDTO::productId,
                OrderItemRequestDTO::quantity,
                Integer::sum,
                LinkedHashMap::new));
    }

    // Uma única consulta para todos os produtos (evita um SELECT por item).
    private Map<UUID, Product> findProducts(Iterable<UUID> ids) {
        Map<UUID, Product> products = productRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));
        for (UUID id : ids) {
            if (!products.containsKey(id)) {
                throw new ProductNotFoundException(id);
            }
        }
        return products;
    }

    private Order getOrder(UUID id) {
        return orderRepository.findWithItemsById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }
}
