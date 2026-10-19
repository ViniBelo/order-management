package br.com.devpasso.order_management.api.mapper.products.response;

import br.com.devpasso.order_management.api.dto.products.response.ProductResponse;
import br.com.devpasso.order_management.application.dto.result.products.ProductResult;
import org.springframework.stereotype.Component;

@Component
public class ProductResponseMapper {
    public ProductResponse toResponse(ProductResult productResult) {
        return new ProductResponse(productResult.id(),
                productResult.name(),
                productResult.description(),
                productResult.price(),
                productResult.stockQuantity());
    }
}
