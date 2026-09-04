package br.com.devpasso.order_management.api.mapper.response;

import br.com.devpasso.order_management.api.dto.response.CreateProductResponse;
import br.com.devpasso.order_management.application.dto.result.CreateProductResult;
import org.springframework.stereotype.Component;

@Component
public class CreateProductResponseMapper {
    public CreateProductResponse toResponse(CreateProductResult createProductResult) {
        return new CreateProductResponse(
                createProductResult.id(),
                createProductResult.name(),
                createProductResult.description(),
                createProductResult.price(),
                createProductResult.stockQuantity(),
                createProductResult.createdAt()
        );
    }
}
