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

import com.gabrielalves.order_processing_system.dto.ProductRequestDTO;
import com.gabrielalves.order_processing_system.dto.ProductResponseDTO;
import com.gabrielalves.order_processing_system.entity.Product;
import com.gabrielalves.order_processing_system.exception.ProductInUseException;
import com.gabrielalves.order_processing_system.exception.ProductNotFoundException;
import com.gabrielalves.order_processing_system.mapper.ProductMapper;
import com.gabrielalves.order_processing_system.repository.OrderRepository;
import com.gabrielalves.order_processing_system.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderRepository orderRepository;

    @Spy
    private ProductMapper productMapper = Mappers.getMapper(ProductMapper.class);

    @InjectMocks
    private ProductService productService;

    private final UUID id = UUID.randomUUID();
    private final ProductRequestDTO request = new ProductRequestDTO("Teclado", new BigDecimal("199.90"), 10);

    @Test
    void shouldCreateProduct() {
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product saved = invocation.getArgument(0);
            ReflectionTestUtils.setField(saved, "id", id);
            return saved;
        });

        ProductResponseDTO response = productService.create(request);

        assertThat(response).isEqualTo(new ProductResponseDTO(id, "Teclado", new BigDecimal("199.90"), 10));
    }

    @Test
    void shouldFindProductById() {
        when(productRepository.findById(id)).thenReturn(Optional.of(product("Teclado", "199.90", 10)));

        assertThat(productService.findById(id).name()).isEqualTo("Teclado");
    }

    @Test
    void shouldThrowWhenProductDoesNotExist() {
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.findById(id))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining(id.toString());
    }

    @Test
    void shouldListProductsPaginated() {
        PageRequest pageable = PageRequest.of(0, 10);
        when(productRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(product("Teclado", "199.90", 10)), pageable, 1));

        assertThat(productService.findAll(pageable).getContent())
                .extracting(ProductResponseDTO::name)
                .containsExactly("Teclado");
    }

    @Test
    void shouldUpdateProduct() {
        Product existing = product("Nome Antigo", "50.00", 1);
        when(productRepository.findById(id)).thenReturn(Optional.of(existing));

        ProductResponseDTO response = productService.update(id, request);

        assertThat(response).isEqualTo(new ProductResponseDTO(id, "Teclado", new BigDecimal("199.90"), 10));
        assertThat(existing.getPrice()).isEqualByComparingTo("199.90");
        assertThat(existing.getStock()).isEqualTo(10);
    }

    @Test
    void shouldThrowWhenUpdatingNonexistentProduct() {
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.update(id, request))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void shouldDeleteProductNotUsedInOrders() {
        Product existing = product("Teclado", "199.90", 10);
        when(productRepository.findById(id)).thenReturn(Optional.of(existing));
        when(orderRepository.existsByItemsProductId(id)).thenReturn(false);

        productService.delete(id);

        verify(productRepository).delete(existing);
    }

    @Test
    void shouldNotDeleteProductUsedInOrders() {
        when(productRepository.findById(id)).thenReturn(Optional.of(product("Teclado", "199.90", 10)));
        when(orderRepository.existsByItemsProductId(id)).thenReturn(true);

        assertThatThrownBy(() -> productService.delete(id))
                .isInstanceOf(ProductInUseException.class);
        verify(productRepository, never()).delete(any());
    }

    @Test
    void shouldThrowWhenDeletingNonexistentProduct() {
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.delete(id))
                .isInstanceOf(ProductNotFoundException.class);
    }

    private Product product(String name, String price, int stock) {
        Product product = new Product(name, new BigDecimal(price), stock);
        ReflectionTestUtils.setField(product, "id", id);
        return product;
    }
}
