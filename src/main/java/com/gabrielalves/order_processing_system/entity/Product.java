package com.gabrielalves.order_processing_system.entity;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price", nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    @Column(name = "stock", nullable = false)
    private Integer stock;

    public Product(String name, BigDecimal price, Integer stock) {
        update(name, price, stock);
    }

    public void update(String name, BigDecimal price, Integer stock) {
        if (price == null || price.signum() < 0) {
            throw new IllegalArgumentException("Preço não pode ser negativo");
        }
        if (stock == null || stock < 0) {
            throw new IllegalArgumentException("Estoque não pode ser negativo");
        }
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public boolean hasStock(int quantity) {
        return stock >= quantity;
    }

    public void decreaseStock(int quantity) {
        requirePositive(quantity);
        if (!hasStock(quantity)) {
            throw new IllegalStateException("Estoque insuficiente para o produto: " + id);
        }
        this.stock -= quantity;
    }

    public void increaseStock(int quantity) {
        requirePositive(quantity);
        this.stock += quantity;
    }

    private static void requirePositive(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }
    }
}
