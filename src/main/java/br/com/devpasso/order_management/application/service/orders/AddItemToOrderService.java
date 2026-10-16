package br.com.devpasso.order_management.application.service.orders;

import br.com.devpasso.order_management.application.dto.command.orders.AddItemToOrderCommand;
import br.com.devpasso.order_management.application.dto.result.orders.AddItemToOrderResult;
import br.com.devpasso.order_management.application.exception.BusinessException;
import br.com.devpasso.order_management.application.mapper.orders.AddItemToOrderResultMapper;
import br.com.devpasso.order_management.application.usecase.orders.AddItemToOrderUseCase;
import br.com.devpasso.order_management.domain.exception.ResourceNotFoundException;
import br.com.devpasso.order_management.domain.model.Order;
import br.com.devpasso.order_management.domain.model.OrderItem;
import br.com.devpasso.order_management.domain.model.Product;
import br.com.devpasso.order_management.domain.repository.OrderRepository;
import br.com.devpasso.order_management.domain.repository.ProductRepository;

public class AddItemToOrderService implements AddItemToOrderUseCase {
    private final AddItemToOrderResultMapper addItemToOrderResultMapper;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public AddItemToOrderService(AddItemToOrderResultMapper addItemToOrderResultMapper,
                                 OrderRepository orderRepository,
                                 ProductRepository productRepository) {
        this.addItemToOrderResultMapper = addItemToOrderResultMapper;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Override
    public AddItemToOrderResult execute(AddItemToOrderCommand command) {
        Order order = orderRepository.findById(command.orderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found for ID: " + command.orderId()));

        Product product = productRepository.findById(command.productId().toString())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found for ID: " + command.productId()));

        if (product.getStockQuantity() < command.quantity()) {
            throw new BusinessException("Unsufficient stock quantity for: " + product.getName() +
                    "Available: " + product.getStockQuantity() +
                    "Requested: " + command.quantity());
        }

        OrderItem item = command.toDomainModel(product.getName(),
                product.getPrice());
        order.items()
                .add(item);
        orderRepository.save(order);

        return addItemToOrderResultMapper.map(item);
    }
}
