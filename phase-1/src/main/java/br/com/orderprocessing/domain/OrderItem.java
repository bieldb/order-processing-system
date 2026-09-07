package br.com.orderprocessing.domain;

import java.math.BigDecimal;

public class OrderItem {
    private Product product;
    private int quantity;
    private BigDecimal unitPrice;

    public Product getProduct() {
        return product;
    }


    public int getQuantity() {
        return quantity;
    }


    public BigDecimal getUnitPrice() {
        return unitPrice;
    }


    public OrderItem(Product product, int quantity) {

        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero");
        }

        this.product = product;
        this.quantity = quantity;
        this.unitPrice = product.getPrice();
    }

    public OrderItem() {
    }

    public BigDecimal getSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
