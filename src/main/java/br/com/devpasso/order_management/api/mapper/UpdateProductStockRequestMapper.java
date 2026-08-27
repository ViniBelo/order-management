package br.com.devpasso.order_management.api.mapper;

import br.com.devpasso.order_management.api.dto.UpdateProductStockRequest;
import br.com.devpasso.order_management.application.dto.command.UpdateProductStockCommand;
import org.springframework.stereotype.Component;

@Component
public class UpdateProductStockRequestMapper {
    public UpdateProductStockCommand toCommand(UpdateProductStockRequest request) {
        return new UpdateProductStockCommand(request.stockQuantity());
    }
}
