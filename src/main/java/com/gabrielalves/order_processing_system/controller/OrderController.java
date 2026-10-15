package com.gabrielalves.order_processing_system.controller;

import java.net.URI;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.gabrielalves.order_processing_system.dto.OrderRequestDTO;
import com.gabrielalves.order_processing_system.dto.OrderResponseDTO;
import com.gabrielalves.order_processing_system.entity.OrderStatus;
import com.gabrielalves.order_processing_system.service.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponseDTO> create(@RequestBody @Valid OrderRequestDTO dto) {
        OrderResponseDTO response = orderService.create(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.findById(id));
    }

    @GetMapping
    public ResponseEntity<Page<OrderResponseDTO>> findAll(
            @RequestParam(required = false) OrderStatus status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(orderService.findAll(status, pageable));
    }

    @PostMapping("/{id}/process")
    public ResponseEntity<OrderResponseDTO> process(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.process(id));
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<OrderResponseDTO> complete(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.complete(id));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<OrderResponseDTO> cancel(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.cancel(id));
    }
}
