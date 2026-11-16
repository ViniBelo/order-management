package br.com.devpasso.order_management.api.mapper.products.request;

import br.com.devpasso.order_management.api.dto.request.products.UpdateProductStockRequest;
import br.com.devpasso.order_management.application.dto.command.products.UpdateProductStockCommand;
import org.springframework.stereotype.Component;

@Component
public class UpdateProductStockRequestMapper {
    public UpdateProductStockCommand toCommand(UpdateProductStockRequest request) {
        return new UpdateProductStockCommand(request.stockQuantity());
    }
}
