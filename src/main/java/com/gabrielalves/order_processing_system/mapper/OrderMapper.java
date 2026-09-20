package com.gabrielalves.order_processing_system.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.gabrielalves.order_processing_system.dto.OrderItemResponseDTO;
import com.gabrielalves.order_processing_system.dto.OrderRequestDTO;
import com.gabrielalves.order_processing_system.dto.OrderResponseDTO;
import com.gabrielalves.order_processing_system.entity.Order;
import com.gabrielalves.order_processing_system.entity.OrderItem;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    // Provisório: o pedido real (cliente, itens, preços, total, status) será montado no OrderService.
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "total", ignore = true)
    Order toEntity(OrderRequestDTO dto);

    @Mapping(source = "customer.id", target = "customerId")
    OrderResponseDTO toResponse(Order order);

    @Mapping(source = "product.id", target = "productId")
    OrderItemResponseDTO toItemResponse(OrderItem item);
}
