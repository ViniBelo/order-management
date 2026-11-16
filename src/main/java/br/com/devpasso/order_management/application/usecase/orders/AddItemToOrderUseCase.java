package br.com.devpasso.order_management.application.usecase.orders;

import br.com.devpasso.order_management.application.dto.command.orders.AddItemToOrderCommand;
import br.com.devpasso.order_management.application.dto.result.orders.OrderItemResult;

public interface AddItemToOrderUseCase {
    OrderItemResult execute(AddItemToOrderCommand command);
}
