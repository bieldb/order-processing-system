package com.gabrielalves.order_processing_system.service;

import org.springframework.stereotype.Service;

import com.gabrielalves.order_processing_system.dto.OrderRequestDTO;
import com.gabrielalves.order_processing_system.dto.OrderResponseDTO;
import com.gabrielalves.order_processing_system.entity.Order;
import com.gabrielalves.order_processing_system.mapper.OrderMapper;
import com.gabrielalves.order_processing_system.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public OrderResponseDTO save(OrderRequestDTO dto) {
        Order order = orderMapper.toEntity(dto);
        order = orderRepository.save(order);
        return orderMapper.toResponse(order);
    }
}
