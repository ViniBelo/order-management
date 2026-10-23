package br.com.devpasso.order_management.application.service.orders;

import br.com.devpasso.order_management.application.dto.command.orders.AddItemToOrderCommand;
import br.com.devpasso.order_management.application.dto.result.orders.OrderItemResult;
import br.com.devpasso.order_management.application.exception.BusinessException;
import br.com.devpasso.order_management.application.mapper.orders.OrderItemResultMapper;
import br.com.devpasso.order_management.application.usecase.orders.AddItemToOrderUseCase;
import br.com.devpasso.order_management.domain.exception.ResourceNotFoundException;
import br.com.devpasso.order_management.domain.model.Order;
import br.com.devpasso.order_management.domain.model.OrderItem;
import br.com.devpasso.order_management.domain.model.Product;
import br.com.devpasso.order_management.domain.repository.OrderRepositoryPort;
import br.com.devpasso.order_management.domain.repository.ProductRepositoryPort;

public class AddItemToOrderService implements AddItemToOrderUseCase {
    private final OrderItemResultMapper orderResultMapper;
    private final OrderRepositoryPort orderRepositoryPort;
    private final ProductRepositoryPort productRepositoryPort;

    public AddItemToOrderService(OrderItemResultMapper orderResultMapper,
                                 OrderRepositoryPort orderRepositoryPort,
                                 ProductRepositoryPort productRepositoryPort) {
        this.orderResultMapper = orderResultMapper;
        this.orderRepositoryPort = orderRepositoryPort;
        this.productRepositoryPort = productRepositoryPort;
    }

    @Override
    public OrderItemResult execute(AddItemToOrderCommand command) {
        Order order = orderRepositoryPort.findById(command.orderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found for ID: " + command.orderId()));

        Product product = productRepositoryPort.findById(command.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found for ID: " + command.productId()));

        if (product.getStockQuantity() < command.quantity()) {
            throw new BusinessException("Unsufficient stock quantity for: " + product.getName() +
                    "Available: " + product.getStockQuantity() +
                    "Requested: " + command.quantity());
        }

        OrderItem item = command.toDomainModel(product.getName(),
                product.getPrice());
        order.addItem(item);
        orderRepositoryPort.save(order);

        return orderResultMapper.map(item);
    }
}
