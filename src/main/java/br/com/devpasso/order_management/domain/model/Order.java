package br.com.devpasso.order_management.domain.model;

import br.com.devpasso.order_management.domain.exception.InvalidOrderStateException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

public class Order {
    private final UUID id;
    private final UUID customerId;
    private OrderStatus status;
    private ArrayList<OrderItem> items;
    private final Instant createdAt;

    public Order(UUID id, UUID customerId) {
        this.id = id;
        this.customerId = customerId;
        this.status = OrderStatus.DRAFT;
        this.items = new ArrayList<>();
        this.createdAt = Instant.now();
    }

    public void addItem(OrderItem item) {
        if (status != OrderStatus.DRAFT)
            throw new InvalidOrderStateException("Order is not in draft status");
        items.add(item);
    }

    public void transitionTo(OrderStatus next) {
        if (!status.canTransitionTo(next))
            throw new InvalidOrderStateException("Invalid order state transition: " + status + " -> " + next);
        this.status = next;
    }

    public BigDecimal total() {
        return items.stream()
                .map(OrderItem::unitPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public UUID id() {
        return id;
    }

    public UUID customerId() {
        return customerId;
    }

    public OrderStatus status() {
        return status;
    }

    public ArrayList<OrderItem> items() {
        return items;
    }

    public Instant createdAt() {
        return createdAt;
    }
}
