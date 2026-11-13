package br.com.devpasso.order_management.infrastructure.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_items")
@SQLDelete(sql = "UPDATE order_items SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class OrderItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne()
    @JoinColumn(name = "order_id",
                referencedColumnName = "id",
                nullable = false)
    private OrderEntity order;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "product_id",
                referencedColumnName = "id",
                nullable = false)
    private ProductEntity product;

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

    public OrderEntity getOrder() {
        return order;
    }

    public ProductEntity getProduct() {
        return product;
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

    public void changeOrder(OrderEntity order) {
        this.order = order;
    }

    public void changeProduct(ProductEntity product) {
        this.product = product;
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
