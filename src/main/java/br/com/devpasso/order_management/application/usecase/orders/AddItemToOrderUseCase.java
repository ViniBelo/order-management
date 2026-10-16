package br.com.devpasso.order_management.application.usecase.orders;

import br.com.devpasso.order_management.application.dto.command.orders.AddItemToOrderCommand;
import br.com.devpasso.order_management.application.dto.result.orders.AddItemToOrderResult;

public interface AddItemToOrderUseCase {
    AddItemToOrderResult execute(AddItemToOrderCommand command);
}
