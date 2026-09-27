package com.gabrielalves.order_processing_system.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gabrielalves.order_processing_system.dto.CustomerRequestDTO;
import com.gabrielalves.order_processing_system.dto.CustomerResponseDTO;
import com.gabrielalves.order_processing_system.entity.Customer;
import com.gabrielalves.order_processing_system.exception.CustomerHasOrdersException;
import com.gabrielalves.order_processing_system.exception.CustomerNotFoundException;
import com.gabrielalves.order_processing_system.exception.EmailAlreadyExistsException;
import com.gabrielalves.order_processing_system.mapper.CustomerMapper;
import com.gabrielalves.order_processing_system.repository.CustomerRepository;
import com.gabrielalves.order_processing_system.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final CustomerMapper customerMapper;

    @Transactional
    public CustomerResponseDTO create(CustomerRequestDTO dto) {
        if (customerRepository.existsByEmail(dto.email())) {
            throw new EmailAlreadyExistsException(dto.email());
        }

        Customer customer = customerMapper.toEntity(dto);
        customer = customerRepository.save(customer);
        return customerMapper.toResponse(customer);
    }

    @Transactional(readOnly = true)
    public CustomerResponseDTO findById(UUID id) {
        return customerMapper.toResponse(getCustomer(id));
    }

    @Transactional(readOnly = true)
    public Page<CustomerResponseDTO> findAll(Pageable pageable) {
        return customerRepository.findAll(pageable).map(customerMapper::toResponse);
    }

    @Transactional
    public CustomerResponseDTO update(UUID id, CustomerRequestDTO dto) {
        Customer customer = getCustomer(id);
        if (customerRepository.existsByEmailAndIdNot(dto.email(), id)) {
            throw new EmailAlreadyExistsException(dto.email());
        }

        // Entidade gerenciada: o dirty checking do Hibernate persiste as alterações no commit.
        customerMapper.updateEntity(dto, customer);
        return customerMapper.toResponse(customer);
    }

    @Transactional
    public void delete(UUID id) {
        Customer customer = getCustomer(id);
        if (orderRepository.existsByCustomerId(id)) {
            throw new CustomerHasOrdersException(id);
        }
        customerRepository.delete(customer);
    }

    private Customer getCustomer(UUID id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
    }
}
