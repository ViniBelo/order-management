package br.com.devpasso.order_management.application.service.orders;

import br.com.devpasso.order_management.application.dto.result.orders.OrderResult;
import br.com.devpasso.order_management.application.mapper.orders.OrderResultMapper;
import br.com.devpasso.order_management.application.usecase.orders.FindOrderByIdUseCase;
import br.com.devpasso.order_management.domain.exception.ResourceNotFoundException;
import br.com.devpasso.order_management.domain.model.Order;
import br.com.devpasso.order_management.domain.repository.OrderRepositoryPort;

import java.util.UUID;

public class FindOrdersService implements FindOrderByIdUseCase {
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
}
