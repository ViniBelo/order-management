package br.com.devpasso.order_management.application.service.orders;

import br.com.devpasso.order_management.application.dto.command.orders.CreateOrderCommand;
import br.com.devpasso.order_management.application.dto.result.orders.OrderResult;
import br.com.devpasso.order_management.application.mapper.orders.OrderResultMapper;
import br.com.devpasso.order_management.application.usecase.orders.CreateOrderUseCase;
import br.com.devpasso.order_management.domain.model.Order;
import br.com.devpasso.order_management.domain.repository.OrderRepositoryPort;

public class CreateOrdersService implements CreateOrderUseCase {
    private final OrderResultMapper orderResultMapper;
    private final OrderRepositoryPort orderRepositoryPort;

    public CreateOrdersService(OrderResultMapper orderResultMapper,
                               OrderRepositoryPort orderRepositoryPort) {
        this.orderResultMapper = orderResultMapper;
        this.orderRepositoryPort = orderRepositoryPort;
    }

    @Override
    public OrderResult execute(CreateOrderCommand command) {
        Order orderToSave = command.toDomainModel();
        Order savedOrder = orderRepositoryPort.save(orderToSave);
        return orderResultMapper.map(savedOrder);
    }
}
