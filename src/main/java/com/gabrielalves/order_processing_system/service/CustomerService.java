package com.gabrielalves.order_processing_system.service;

import org.springframework.stereotype.Service;

import com.gabrielalves.order_processing_system.dto.CustomerRequestDTO;
import com.gabrielalves.order_processing_system.dto.CustomerResponseDTO;
import com.gabrielalves.order_processing_system.entity.Customer;
import com.gabrielalves.order_processing_system.exception.EmailAlreadyExistsException;
import com.gabrielalves.order_processing_system.mapper.CustomerMapper;
import com.gabrielalves.order_processing_system.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor 
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerResponseDTO create(CustomerRequestDTO dto) {
        if (customerRepository.existsByEmail(dto.email())) {
            throw new EmailAlreadyExistsException(dto.email());
        }

        Customer customer = customerMapper.toEntity(dto);
        customer = customerRepository.save(customer);
        return customerMapper.toResponse(customer);
    }
}
