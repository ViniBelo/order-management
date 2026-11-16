package br.com.devpasso.order_management.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class Product {
    private final UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private final Instant createdAt;

    public Product(UUID id,
                   String name,
                   String description,
                   BigDecimal price,
                   Integer stockQuantity,
                   Instant createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void changeName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void changeDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void changePrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void changeStockQuantity(Integer stockQuantity) {
        if (stockQuantity == null || stockQuantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }
        this.stockQuantity = stockQuantity;
    }

    public void decrementStockQuantity(Integer quantity) {
        validateQuantity(quantity);
        if (stockQuantity < quantity) {
            throw new IllegalArgumentException("Not enough stock quantity");
        }
        stockQuantity -= quantity;
    }

    public void incrementStockQuantity(Integer quantity) {
        validateQuantity(quantity);
        stockQuantity += quantity;
    }

    private void validateQuantity(Integer quantity) {
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
