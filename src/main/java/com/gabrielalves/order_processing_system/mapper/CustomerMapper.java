package com.gabrielalves.order_processing_system.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.gabrielalves.order_processing_system.dto.CustomerRequestDTO;
import com.gabrielalves.order_processing_system.dto.CustomerResponseDTO;
import com.gabrielalves.order_processing_system.entity.Customer;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    @Mapping(target = "id", ignore = true)
    Customer toEntity(CustomerRequestDTO dto);

    CustomerResponseDTO toResponse(Customer customer);
}
