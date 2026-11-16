package br.com.devpasso.order_management.application.usecase.orders;

import br.com.devpasso.order_management.application.dto.command.orders.FilterOrdersByStatusCommand;
import br.com.devpasso.order_management.application.dto.result.PaginatedResult;
import br.com.devpasso.order_management.application.dto.result.orders.OrderResult;
import br.com.devpasso.order_management.domain.common.PaginationQuery;

public interface ListOrdersUseCase {
    PaginatedResult<OrderResult> execute(PaginationQuery query, FilterOrdersByStatusCommand status);
}
