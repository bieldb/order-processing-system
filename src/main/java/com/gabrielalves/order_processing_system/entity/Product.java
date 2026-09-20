package com.gabrielalves.order_processing_system.entity;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Table(name = "products")
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
public class Product {

    @Id 
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    @Setter(AccessLevel.NONE)
    @Positive 
    private BigDecimal price;
    @Setter(AccessLevel.NONE)
    @PositiveOrZero 
    private Integer stock;
}
