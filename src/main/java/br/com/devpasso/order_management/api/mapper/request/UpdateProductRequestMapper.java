package br.com.devpasso.order_management.api.mapper.request;

import br.com.devpasso.order_management.api.dto.request.UpdateProductRequest;
import br.com.devpasso.order_management.application.dto.command.UpdateProductCommand;
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
