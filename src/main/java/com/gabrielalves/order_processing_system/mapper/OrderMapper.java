package com.gabrielalves.order_processing_system.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.gabrielalves.order_processing_system.dto.OrderItemResponseDTO;
import com.gabrielalves.order_processing_system.dto.OrderResponseDTO;
import com.gabrielalves.order_processing_system.entity.Order;
import com.gabrielalves.order_processing_system.entity.OrderItem;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    // O Order é montado pelo OrderService (cliente, itens com preço congelado, total), por isso não há toEntity.
    @Mapping(source = "customer.id", target = "customerId")
    OrderResponseDTO toResponse(Order order);

    @Mapping(source = "product.id", target = "productId")
    OrderItemResponseDTO toItemResponse(OrderItem item);
}
