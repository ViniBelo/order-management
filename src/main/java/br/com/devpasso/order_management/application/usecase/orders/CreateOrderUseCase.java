package br.com.devpasso.order_management.application.usecase.orders;

import br.com.devpasso.order_management.application.dto.command.orders.CreateOrderCommand;
import br.com.devpasso.order_management.application.dto.result.orders.OrderResult;

public interface CreateOrderUseCase {
    OrderResult execute(CreateOrderCommand command);
}
