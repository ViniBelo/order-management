package br.com.devpasso.order_management.domain.repository;

import br.com.devpasso.order_management.domain.model.Order;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(UUID id);
}
