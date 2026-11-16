package br.com.devpasso.order_management.infrastructure.persistence.adapter.mapper;

import br.com.devpasso.order_management.domain.model.OrderStatus;
import br.com.devpasso.order_management.infrastructure.persistence.entity.OrderStatusEntity;
import org.springframework.stereotype.Component;

@Component
public class OrderStatusMapper {
    public OrderStatusEntity toEntity(OrderStatus status) {
        return OrderStatusEntity.valueOf(status.name());
    }

    public OrderStatus toDomain(OrderStatusEntity status) {
        return OrderStatus.valueOf(status.name());
    }
}
