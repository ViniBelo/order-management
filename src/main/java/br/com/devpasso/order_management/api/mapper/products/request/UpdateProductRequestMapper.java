package br.com.devpasso.order_management.api.mapper.products.request;

import br.com.devpasso.order_management.api.dto.request.products.UpdateProductRequest;
import br.com.devpasso.order_management.application.dto.command.products.UpdateProductCommand;
import org.springframework.stereotype.Component;

@Component
public class UpdateProductRequestMapper {
    public UpdateProductCommand toCommand(UpdateProductRequest updateProductRequest) {
        return new UpdateProductCommand(
                updateProductRequest.name(),
                updateProductRequest.description(),
                updateProductRequest.price()
        );
    }
}
