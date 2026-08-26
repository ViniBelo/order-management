package br.com.devpasso.order_management.api.mapper;

import br.com.devpasso.order_management.api.dto.CreateProductRequest;
import br.com.devpasso.order_management.application.dto.command.CreateProductCommand;
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
