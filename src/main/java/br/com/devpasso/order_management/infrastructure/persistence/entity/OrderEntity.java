package br.com.devpasso.order_management.infrastructure.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.type.PostgreSQLEnumJdbcType;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class OrderEntity {
    @Id
    private UUID id;

    @Column(name = "customer_id",
            nullable = false)
    private UUID customerId;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "status",
            columnDefinition = "order_status",
            nullable = false)
    private OrderStatusEntity status;

    @OneToMany(mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<OrderItemEntity> items;

    @Column(name = "created_at",
            nullable = false,
            updatable = false)
    private Instant createdAt;

    public OrderEntity() {
        items = new ArrayList<>();
    }

    public void addItem(OrderItemEntity item) {
        items.add(item);
        item.changeOrder(this);
    }

    public UUID getId() {
        return id;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public OrderStatusEntity getStatus() {
        return status;
    }

    public List<OrderItemEntity> getItems() {
        return items;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void changeId(UUID id) {
        this.id = id;
    }

    public void changeCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public void changeStatus(OrderStatusEntity status) {
        this.status = status;
    }

    public void changeCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
