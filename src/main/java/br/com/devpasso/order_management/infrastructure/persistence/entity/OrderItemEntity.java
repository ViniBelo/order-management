package br.com.devpasso.order_management.infrastructure.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_items")
public class OrderItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "order_id",
            nullable = false)
    private UUID orderId;

    @Column(name = "product_id",
            nullable = false)
    private UUID productId;

    @Size(max = 255)
    @Column(name = "product_name",
            nullable = false)
    private String productName;

    @Column(name = "unit_price",
            nullable = false,
            precision = 10,
            scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "quantity",
            nullable = false)
    private Integer quantity;

    public UUID getId() {
        return id;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public UUID getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void changeId(UUID id) {
        this.id = id;
    }

    public void changeOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public void changeProductId(UUID productId) {
        this.productId = productId;
    }

    public void changeProductName(String productName) {
        this.productName = productName;
    }

    public void changeUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public void changeQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
