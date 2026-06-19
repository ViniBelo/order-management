package br.com.devpasso.order_management.infrastructure.persistence.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false, unique = true)
    private String name;
    @Column()
    private String description;
    @Column(nullable = false)
    private BigDecimal price;
    @Column(nullable = false)
    private Integer stockQuantity;
    @CreatedDate
    @Column(name = "created_at",
            nullable = false,
            updatable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void changeName(String name) {
        this.name = name;
    }

    public void changeDescription(String description) {
        this.description = description;
    }

    public void changePrice(BigDecimal price) {
        this.price = price;
    }

    public void changeStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
}
