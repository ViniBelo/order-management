package br.com.devpasso.order_management.api.mapper.products.response;

import br.com.devpasso.order_management.api.dto.response.products.CreateProductResponse;
import br.com.devpasso.order_management.application.dto.result.products.CreateProductResult;
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
