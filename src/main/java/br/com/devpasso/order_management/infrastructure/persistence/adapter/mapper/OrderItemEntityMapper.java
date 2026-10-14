package br.com.devpasso.order_management.infrastructure.persistence.adapter.mapper;

import br.com.devpasso.order_management.domain.model.OrderItem;
import br.com.devpasso.order_management.infrastructure.persistence.entity.OrderItemEntity;
import org.springframework.stereotype.Component;

@Component
public class OrderItemEntityMapper {
    public OrderItemEntity toEntity(OrderItem item) {
        OrderItemEntity orderItemEntity = new OrderItemEntity();
        orderItemEntity.changeId(item.id());
        orderItemEntity.changeProductId(item.productId());
        orderItemEntity.changeProductName(item.productName());
        orderItemEntity.changeUnitPrice(item.unitPrice());
        orderItemEntity.changeQuantity(item.quantity());
        return orderItemEntity;
    }

    public OrderItem toDomain(OrderItemEntity item) {
        return new OrderItem(
                item.getId(),
                item.getProductId(),
                item.getProductName(),
                item.getUnitPrice(),
                item.getQuantity()
        );
    }
}
