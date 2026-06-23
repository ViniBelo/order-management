package br.com.devpasso.order_management.application.mapper;

import br.com.devpasso.order_management.application.dto.ProductResponse;
import br.com.devpasso.order_management.domain.model.Product;
import org.springframework.stereotype.Service;

@Service
public class ProductResponseMapper {
    public ProductResponse toResponse(Product product) {
        return new ProductResponse(product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity());
    }
}
