package com.gabrielalves.order_processing_system.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

import com.gabrielalves.order_processing_system.dto.CustomerRequestDTO;
import com.gabrielalves.order_processing_system.dto.CustomerResponseDTO;
import com.gabrielalves.order_processing_system.entity.Customer;
import com.gabrielalves.order_processing_system.exception.CustomerHasOrdersException;
import com.gabrielalves.order_processing_system.exception.CustomerNotFoundException;
import com.gabrielalves.order_processing_system.exception.EmailAlreadyExistsException;
import com.gabrielalves.order_processing_system.mapper.CustomerMapper;
import com.gabrielalves.order_processing_system.repository.CustomerRepository;
import com.gabrielalves.order_processing_system.repository.OrderRepository;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private OrderRepository orderRepository;

    @Spy
    private CustomerMapper customerMapper = Mappers.getMapper(CustomerMapper.class);

    @InjectMocks
    private CustomerService customerService;

    private final UUID id = UUID.randomUUID();
    private final CustomerRequestDTO request = new CustomerRequestDTO("Maria Silva", "maria@email.com");

    @Test
    void shouldCreateCustomer() {
        when(customerRepository.existsByEmail(request.email())).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer saved = invocation.getArgument(0);
            ReflectionTestUtils.setField(saved, "id", id);
            return saved;
        });

        CustomerResponseDTO response = customerService.create(request);

        assertThat(response).isEqualTo(new CustomerResponseDTO(id, "Maria Silva", "maria@email.com"));
    }

    @Test
    void shouldNotCreateCustomerWithDuplicatedEmail() {
        when(customerRepository.existsByEmail(request.email())).thenReturn(true);

        assertThatThrownBy(() -> customerService.create(request))
                .isInstanceOf(EmailAlreadyExistsException.class);
        verify(customerRepository, never()).save(any());
    }

    @Test
    void shouldFindCustomerById() {
        when(customerRepository.findById(id)).thenReturn(Optional.of(customer("Maria Silva", "maria@email.com")));

        assertThat(customerService.findById(id).name()).isEqualTo("Maria Silva");
    }

    @Test
    void shouldThrowWhenCustomerDoesNotExist() {
        when(customerRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.findById(id))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining(id.toString());
    }

    @Test
    void shouldListCustomersPaginated() {
        PageRequest pageable = PageRequest.of(0, 10);
        when(customerRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(customer("Maria Silva", "maria@email.com")), pageable, 1));

        assertThat(customerService.findAll(pageable).getContent())
                .extracting(CustomerResponseDTO::email)
                .containsExactly("maria@email.com");
    }

    @Test
    void shouldUpdateCustomer() {
        Customer existing = customer("Nome Antigo", "antigo@email.com");
        when(customerRepository.findById(id)).thenReturn(Optional.of(existing));
        when(customerRepository.existsByEmailAndIdNot(request.email(), id)).thenReturn(false);

        CustomerResponseDTO response = customerService.update(id, request);

        assertThat(response).isEqualTo(new CustomerResponseDTO(id, "Maria Silva", "maria@email.com"));
        assertThat(existing.getName()).isEqualTo("Maria Silva");
    }

    @Test
    void shouldNotUpdateCustomerToAnEmailUsedByAnotherCustomer() {
        Customer existing = customer("Nome Antigo", "antigo@email.com");
        when(customerRepository.findById(id)).thenReturn(Optional.of(existing));
        when(customerRepository.existsByEmailAndIdNot(request.email(), id)).thenReturn(true);

        assertThatThrownBy(() -> customerService.update(id, request))
                .isInstanceOf(EmailAlreadyExistsException.class);
        assertThat(existing.getEmail()).isEqualTo("antigo@email.com");
    }

    @Test
    void shouldDeleteCustomerWithoutOrders() {
        Customer existing = customer("Maria Silva", "maria@email.com");
        when(customerRepository.findById(id)).thenReturn(Optional.of(existing));
        when(orderRepository.existsByCustomerId(id)).thenReturn(false);

        customerService.delete(id);

        verify(customerRepository).delete(existing);
    }

    @Test
    void shouldNotDeleteCustomerWithOrders() {
        when(customerRepository.findById(id)).thenReturn(Optional.of(customer("Maria Silva", "maria@email.com")));
        when(orderRepository.existsByCustomerId(id)).thenReturn(true);

        assertThatThrownBy(() -> customerService.delete(id))
                .isInstanceOf(CustomerHasOrdersException.class);
        verify(customerRepository, never()).delete(any());
    }

    @Test
    void shouldThrowWhenDeletingNonexistentCustomer() {
        when(customerRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.delete(id))
                .isInstanceOf(CustomerNotFoundException.class);
    }

    private Customer customer(String name, String email) {
        Customer customer = new Customer();
        ReflectionTestUtils.setField(customer, "id", id);
        customer.setName(name);
        customer.setEmail(email);
        return customer;
    }
}
