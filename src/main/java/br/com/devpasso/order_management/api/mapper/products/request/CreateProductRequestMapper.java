package br.com.devpasso.order_management.api.mapper.products.request;

import br.com.devpasso.order_management.api.dto.products.request.CreateProductRequest;
import br.com.devpasso.order_management.application.dto.command.products.CreateProductCommand;
import org.springframework.stereotype.Component;

@Component
public class CreateProductRequestMapper {
    public CreateProductCommand toCommand(CreateProductRequest createProductRequest) {
        return new CreateProductCommand(
                createProductRequest.name(),
                createProductRequest.description(),
                createProductRequest.price(),
                createProductRequest.stockQuantity()
        );
    }
}
