package br.com.orderprocessing.domain;

import java.math.BigDecimal;
import java.util.UUID;

import br.com.orderprocessing.exception.InsufficientStockException;

public class Product {
    private UUID id;
    private String name;
    private BigDecimal price;
    private int stock;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }


    public int getStock() {
        return stock;
    }


    public Product(UUID id, String name, BigDecimal price, int stock) {
        if(stock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public Product() {
    }

    public void increaseStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero");
        }

        stock += quantity;
    }

    public void decreaseStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        if (quantity > stock) {
            throw new InsufficientStockException("Insufficient stock");
        }

        stock -= quantity;
    }
}
