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

import com.gabrielalves.order_processing_system.dto.CustomerRequestDTO;
import com.gabrielalves.order_processing_system.dto.CustomerResponseDTO;
import com.gabrielalves.order_processing_system.exception.CustomerHasOrdersException;
import com.gabrielalves.order_processing_system.exception.CustomerNotFoundException;
import com.gabrielalves.order_processing_system.exception.EmailAlreadyExistsException;
import com.gabrielalves.order_processing_system.service.CustomerService;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    private static final String VALID_BODY = """
            {"name": "Maria Silva", "email": "maria@email.com"}
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerService customerService;

    private final UUID id = UUID.randomUUID();
    private final CustomerResponseDTO response = new CustomerResponseDTO(id, "Maria Silva", "maria@email.com");

    @Test
    void shouldCreateCustomerAndReturnLocation() throws Exception {
        when(customerService.create(any())).thenReturn(response);

        mockMvc.perform(post("/customers").contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/customers/" + id))
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.email").value("maria@email.com"));
    }

    @Test
    void shouldReturn400WhenCreateBodyIsInvalid() throws Exception {
        mockMvc.perform(post("/customers").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"\", \"email\": \"nao-e-email\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields").isArray());

        verifyNoInteractions(customerService);
    }

    @Test
    void shouldReturn409WhenEmailAlreadyExists() throws Exception {
        when(customerService.create(any())).thenThrow(new EmailAlreadyExistsException("maria@email.com"));

        mockMvc.perform(post("/customers").contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldFindCustomerById() throws Exception {
        when(customerService.findById(id)).thenReturn(response);

        mockMvc.perform(get("/customers/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Maria Silva"));
    }

    @Test
    void shouldReturn404WhenCustomerDoesNotExist() throws Exception {
        when(customerService.findById(id)).thenThrow(new CustomerNotFoundException(id));

        mockMvc.perform(get("/customers/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.path").value("/customers/" + id));
    }

    @Test
    void shouldListCustomersWithDefaultPaginationSortedByName() throws Exception {
        Pageable expected = PageRequest.of(0, 20, Sort.by("name"));
        when(customerService.findAll(expected)).thenReturn(new PageImpl<>(List.of(response), expected, 1));

        mockMvc.perform(get("/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(id.toString()))
                .andExpect(jsonPath("$.page.totalElements").value(1))
                .andExpect(jsonPath("$.page.size").value(20));
    }

    @Test
    void shouldUpdateCustomer() throws Exception {
        when(customerService.update(eq(id), any(CustomerRequestDTO.class))).thenReturn(response);

        mockMvc.perform(put("/customers/{id}", id).contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("maria@email.com"));
    }

    @Test
    void shouldDeleteCustomer() throws Exception {
        mockMvc.perform(delete("/customers/{id}", id))
                .andExpect(status().isNoContent());

        verify(customerService).delete(id);
    }

    @Test
    void shouldReturn409WhenDeletingCustomerWithOrders() throws Exception {
        doThrow(new CustomerHasOrdersException(id)).when(customerService).delete(id);

        mockMvc.perform(delete("/customers/{id}", id))
                .andExpect(status().isConflict());
    }
}
