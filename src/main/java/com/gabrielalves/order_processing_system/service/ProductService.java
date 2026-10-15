package com.gabrielalves.order_processing_system.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gabrielalves.order_processing_system.dto.ProductRequestDTO;
import com.gabrielalves.order_processing_system.dto.ProductResponseDTO;
import com.gabrielalves.order_processing_system.entity.Product;
import com.gabrielalves.order_processing_system.exception.ProductInUseException;
import com.gabrielalves.order_processing_system.exception.ProductNotFoundException;
import com.gabrielalves.order_processing_system.mapper.ProductMapper;
import com.gabrielalves.order_processing_system.repository.OrderRepository;
import com.gabrielalves.order_processing_system.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final ProductMapper productMapper;

    @Transactional
    public ProductResponseDTO create(ProductRequestDTO dto) {
        Product product = new Product(dto.name(), dto.price(), dto.stock());
        product = productRepository.save(product);
        return productMapper.toResponse(product);
    }

    @Transactional(readOnly = true)
    public ProductResponseDTO findById(UUID id) {
        return productMapper.toResponse(getProduct(id));
    }

    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> findAll(Pageable pageable) {
        return productRepository.findAll(pageable).map(productMapper::toResponse);
    }

    @Transactional
    public ProductResponseDTO update(UUID id, ProductRequestDTO dto) {
        Product product = getProduct(id);

        // Alterar o preço não afeta pedidos existentes: o OrderItem guarda o preço do momento da compra.
        product.update(dto.name(), dto.price(), dto.stock());
        return productMapper.toResponse(product);
    }

    @Transactional
    public void delete(UUID id) {
        Product product = getProduct(id);
        // Preserva o histórico: itens de pedido referenciam o produto (FK sem cascade).
        if (orderRepository.existsByItemsProductId(id)) {
            throw new ProductInUseException(id);
        }
        productRepository.delete(product);
    }

    private Product getProduct(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }
}
