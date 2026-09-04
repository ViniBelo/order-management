package br.com.devpasso.order_management.application.mapper;

import br.com.devpasso.order_management.application.dto.result.CreateProductResult;
import br.com.devpasso.order_management.domain.model.Product;
import org.springframework.stereotype.Component;

@Component
public class CreateProductResultMapper {
    public CreateProductResult toResult(Product product) {
        return new CreateProductResult(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getCreatedAt()
        );
    }
}
