package br.com.devpasso.order_management.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "customer_id",
            nullable = false)
    private UUID customerId;

    @Column(name = "status",
            nullable = false)
    private OrderStatusEntity status;

    @OneToMany(mappedBy = "order",
            cascade = CascadeType.ALL)
    private List<OrderItemEntity> items;

    @Column(name = "created_at",
            nullable = false)
    private Instant createdAt;

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

    public void changeItems(List<OrderItemEntity> items) {
        this.items = items;
    }

    public void changeCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
