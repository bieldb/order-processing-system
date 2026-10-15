package com.gabrielalves.order_processing_system.mapper;

import org.mapstruct.Mapper;

import com.gabrielalves.order_processing_system.dto.ProductResponseDTO;
import com.gabrielalves.order_processing_system.entity.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    // Criação e atualização passam pelo construtor/update() do Product, que validam as invariantes de preço e estoque.
    ProductResponseDTO toResponse(Product product);
}
