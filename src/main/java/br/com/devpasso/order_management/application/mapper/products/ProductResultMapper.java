package br.com.devpasso.order_management.application.mapper.products;

import br.com.devpasso.order_management.application.dto.result.products.ProductResult;
import br.com.devpasso.order_management.domain.model.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductResultMapper {
    public ProductResult toResult(Product product) {
        return new ProductResult(product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity());
    }
}
