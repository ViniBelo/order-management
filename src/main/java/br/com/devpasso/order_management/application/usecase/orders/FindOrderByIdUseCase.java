package br.com.devpasso.order_management.application.usecase.orders;

import br.com.devpasso.order_management.application.dto.result.orders.OrderResult;

import java.util.UUID;

public interface FindOrderByIdUseCase {
    OrderResult execute(UUID id);
}
