package br.com.devpasso.order_management.infrastructure.persistence.adapter.mapper;

import br.com.devpasso.order_management.domain.model.OrderItem;
import br.com.devpasso.order_management.infrastructure.persistence.entity.OrderItemEntity;
import org.springframework.stereotype.Component;

@Component
public class OrderItemEntityMapper {
    private final ProductEntityMapper productEntityMapper;
    private final ProductModelMapper productModelMapper;

    public OrderItemEntityMapper(ProductEntityMapper productEntityMapper,
                                 ProductModelMapper productModelMapper) {
        this.productEntityMapper = productEntityMapper;
        this.productModelMapper = productModelMapper;
    }

    public OrderItemEntity toEntity(OrderItem item) {
        OrderItemEntity orderItemEntity = new OrderItemEntity();
        orderItemEntity.changeId(item.id());
        orderItemEntity.changeProduct(productEntityMapper.toEntity(item.product()));
        orderItemEntity.changeProductName(item.productName());
        orderItemEntity.changeUnitPrice(item.unitPrice());
        orderItemEntity.changeQuantity(item.quantity());
        return orderItemEntity;
    }

    public OrderItem toDomain(OrderItemEntity item) {
        return new OrderItem(
                item.getId(),
                productModelMapper.toModel(item.getProduct()),
                item.getProductName(),
                item.getUnitPrice(),
                item.getQuantity()
        );
    }
}
