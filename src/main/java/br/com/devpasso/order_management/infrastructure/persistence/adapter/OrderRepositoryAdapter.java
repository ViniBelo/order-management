package br.com.devpasso.order_management.infrastructure.persistence.adapter;

import br.com.devpasso.order_management.domain.common.PaginatedQueryResult;
import br.com.devpasso.order_management.domain.common.PaginationQuery;
import br.com.devpasso.order_management.domain.model.Order;
import br.com.devpasso.order_management.domain.model.OrderStatus;
import br.com.devpasso.order_management.domain.repository.OrderRepositoryPort;
import br.com.devpasso.order_management.infrastructure.persistence.adapter.mapper.InfraPaginationMapper;
import br.com.devpasso.order_management.infrastructure.persistence.adapter.mapper.OrderEntityMapper;
import br.com.devpasso.order_management.infrastructure.persistence.entity.OrderEntity;
import br.com.devpasso.order_management.infrastructure.persistence.entity.OrderStatusEntity;
import br.com.devpasso.order_management.infrastructure.persistence.repository.OrderJpaRepository;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class OrderRepositoryAdapter implements OrderRepositoryPort {
    private final OrderJpaRepository orderJpaRepository;
    private final InfraPaginationMapper paginationMapper;
    private final OrderEntityMapper orderEntityMapper;

    public OrderRepositoryAdapter(OrderJpaRepository orderJpaRepository,
                                  InfraPaginationMapper paginationMapper,
                                  OrderEntityMapper orderEntityMapper) {
        this.orderJpaRepository = orderJpaRepository;
        this.paginationMapper = paginationMapper;
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

    @Override
    public boolean existsById(UUID id) {
        return orderJpaRepository.existsById(id);
    }

    @Override
    public PaginatedQueryResult<Order> findByStatus(PaginationQuery paginationQuery, OrderStatus status) {
        Page<OrderEntity> orderEntities = orderJpaRepository.findAllByStatus(
                paginationMapper.toSpringPageable(paginationQuery),
                OrderStatusEntity.valueOf(status.name())
        );

        List<Order> orders = orderEntities.getContent()
                .stream()
                .map(orderEntityMapper::toDomain)
                .toList();

        return paginationMapper.toDomainResult(orderEntities, orders);
    }

    @Override
    public void deleteById(UUID id) {
        orderJpaRepository.deleteById(id);
    }
}
