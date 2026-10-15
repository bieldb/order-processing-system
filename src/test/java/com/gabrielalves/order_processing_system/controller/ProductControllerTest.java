package com.gabrielalves.order_processing_system.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.gabrielalves.order_processing_system.dto.ProductRequestDTO;
import com.gabrielalves.order_processing_system.dto.ProductResponseDTO;
import com.gabrielalves.order_processing_system.exception.ProductInUseException;
import com.gabrielalves.order_processing_system.exception.ProductNotFoundException;
import com.gabrielalves.order_processing_system.service.ProductService;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    private static final String VALID_BODY = """
            {"name": "Teclado", "price": 199.90, "stock": 10}
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    private final UUID id = UUID.randomUUID();
    private final ProductResponseDTO response = new ProductResponseDTO(id, "Teclado", new BigDecimal("199.90"), 10);

    @Test
    void shouldCreateProductAndReturnLocation() throws Exception {
        when(productService.create(any())).thenReturn(response);

        mockMvc.perform(post("/products").contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/products/" + id))
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.price").value(199.90))
                .andExpect(jsonPath("$.stock").value(10));
    }

    @Test
    void shouldReturn400WhenCreateBodyIsInvalid() throws Exception {
        mockMvc.perform(post("/products").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"\", \"price\": -1, \"stock\": -5}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.length()").value(3));

        verifyNoInteractions(productService);
    }

    @Test
    void shouldReturn400WhenPriceHasMoreThanTwoDecimals() throws Exception {
        mockMvc.perform(post("/products").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Teclado\", \"price\": 10.999, \"stock\": 1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields[0].field").value("price"));

        verifyNoInteractions(productService);
    }

    @Test
    void shouldFindProductById() throws Exception {
        when(productService.findById(id)).thenReturn(response);

        mockMvc.perform(get("/products/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Teclado"));
    }

    @Test
    void shouldReturn404WhenProductDoesNotExist() throws Exception {
        when(productService.findById(id)).thenThrow(new ProductNotFoundException(id));

        mockMvc.perform(get("/products/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.path").value("/products/" + id));
    }

    @Test
    void shouldListProductsWithDefaultPaginationSortedByName() throws Exception {
        Pageable expected = PageRequest.of(0, 20, Sort.by("name"));
        when(productService.findAll(expected)).thenReturn(new PageImpl<>(List.of(response), expected, 1));

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(id.toString()))
                .andExpect(jsonPath("$.page.totalElements").value(1))
                .andExpect(jsonPath("$.page.size").value(20));
    }

    @Test
    void shouldUpdateProduct() throws Exception {
        when(productService.update(eq(id), any(ProductRequestDTO.class))).thenReturn(response);

        mockMvc.perform(put("/products/{id}", id).contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Teclado"));
    }

    @Test
    void shouldDeleteProduct() throws Exception {
        mockMvc.perform(delete("/products/{id}", id))
                .andExpect(status().isNoContent());

        verify(productService).delete(id);
    }

    @Test
    void shouldReturn409WhenDeletingProductUsedInOrders() throws Exception {
        doThrow(new ProductInUseException(id)).when(productService).delete(id);

        mockMvc.perform(delete("/products/{id}", id))
                .andExpect(status().isConflict());
    }
}
