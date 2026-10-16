package br.com.devpasso.order_management.application.service.orders;

import br.com.devpasso.order_management.application.dto.command.orders.CreateOrderCommand;
import br.com.devpasso.order_management.application.dto.result.orders.OrderResult;
import br.com.devpasso.order_management.application.mapper.orders.OrderResultMapper;
import br.com.devpasso.order_management.application.usecase.orders.CreateOrderUseCase;
import br.com.devpasso.order_management.domain.model.Order;
import br.com.devpasso.order_management.domain.repository.OrderRepository;

public class CreateOrdersService implements CreateOrderUseCase {
    private final OrderResultMapper orderResultMapper;
    private final OrderRepository orderRepository;

    public CreateOrdersService(OrderResultMapper orderResultMapper,
                               OrderRepository orderRepository) {
        this.orderResultMapper = orderResultMapper;
        this.orderRepository = orderRepository;
    }

    @Override
    public OrderResult execute(CreateOrderCommand command) {
        Order orderToSave = command.toDomainModel();
        Order savedOrder = orderRepository.save(orderToSave);
        return orderResultMapper.map(savedOrder);
    }
}
