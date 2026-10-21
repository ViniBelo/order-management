package br.com.devpasso.order_management.application.service.orders;

import br.com.devpasso.order_management.application.dto.command.orders.FilterOrdersByStatusCommand;
import br.com.devpasso.order_management.application.dto.result.PaginatedResult;
import br.com.devpasso.order_management.application.dto.result.orders.OrderResult;
import br.com.devpasso.order_management.application.mapper.orders.OrderResultMapper;
import br.com.devpasso.order_management.application.usecase.orders.FindOrderByIdUseCase;
import br.com.devpasso.order_management.application.usecase.orders.ListOrdersUseCase;
import br.com.devpasso.order_management.domain.common.PaginatedQueryResult;
import br.com.devpasso.order_management.domain.common.PaginationQuery;
import br.com.devpasso.order_management.domain.exception.ResourceNotFoundException;
import br.com.devpasso.order_management.domain.model.Order;
import br.com.devpasso.order_management.domain.model.OrderStatus;
import br.com.devpasso.order_management.domain.repository.OrderRepositoryPort;

import java.util.List;
import java.util.UUID;

public class FindOrdersService implements FindOrderByIdUseCase,
        ListOrdersUseCase {
    private final OrderResultMapper orderResultMapper;
    private final OrderRepositoryPort orderRepositoryPort;

    public FindOrdersService(OrderResultMapper orderResultMapper,
                             OrderRepositoryPort orderRepositoryPort) {
        this.orderResultMapper = orderResultMapper;
        this.orderRepositoryPort = orderRepositoryPort;
    }

    @Override
    public OrderResult execute(UUID id) {
        Order order = orderRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found for ID: " + id));
        return orderResultMapper.map(order);
    }

    @Override
    public PaginatedResult<OrderResult> execute(PaginationQuery query,
                                                FilterOrdersByStatusCommand status) {
        PaginatedQueryResult<Order> orders = orderRepositoryPort.findByStatus(query,
                OrderStatus.valueOf(status.name()));
        List<OrderResult> ordersResult = orders.content()
                .stream()
                .map(orderResultMapper::map)
                .toList();
        return PaginatedResult.from(orders, ordersResult);
    }
}
