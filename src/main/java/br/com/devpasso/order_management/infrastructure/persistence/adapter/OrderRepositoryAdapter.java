package br.com.devpasso.order_management.infrastructure.persistence.adapter;

import br.com.devpasso.order_management.domain.model.Order;
import br.com.devpasso.order_management.domain.repository.OrderRepositoryPort;
import br.com.devpasso.order_management.infrastructure.persistence.adapter.mapper.OrderEntityMapper;
import br.com.devpasso.order_management.infrastructure.persistence.entity.OrderEntity;
import br.com.devpasso.order_management.infrastructure.persistence.repository.OrderJpaRepository;

import java.util.Optional;
import java.util.UUID;

public class OrderRepositoryAdapter implements OrderRepositoryPort {
    private final OrderJpaRepository orderJpaRepository;
    private final OrderEntityMapper orderEntityMapper;

    public OrderRepositoryAdapter(OrderJpaRepository orderJpaRepository,
                                  OrderEntityMapper orderEntityMapper) {
        this.orderJpaRepository = orderJpaRepository;
        this.orderEntityMapper = orderEntityMapper;
    }

    @Override
    public Order save(Order order) {
        OrderEntity orderEntity = orderEntityMapper.toEntity(order);
        setOrderToOrderItemIfNeeded(orderEntity);
        OrderEntity savedOrderEntity = orderJpaRepository.save(orderEntity);
        return orderEntityMapper.toDomain(savedOrderEntity);
    }

    private void setOrderToOrderItemIfNeeded(OrderEntity orderEntity) {
        orderEntity.getItems()
                .forEach((item) -> {
                    if (item.getOrder() == null)
                        item.changeOrder(orderEntity);
                });
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return orderJpaRepository.findById(id)
                .map(orderEntityMapper::toDomain);
    }
}
