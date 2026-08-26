package br.com.devpasso.order_management.infrastructure.persistence.adapter.mapper;

import br.com.devpasso.order_management.domain.model.Product;
import br.com.devpasso.order_management.infrastructure.persistence.entity.ProductEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductEntityMapper {
    public ProductEntity toEntity(Product product) {
        ProductEntity productEntity = new ProductEntity();
        productEntity.changeId(product.getId());
        productEntity.changeName(product.getName());
        productEntity.changeDescription(product.getDescription());
        productEntity.changePrice(product.getPrice());
        productEntity.changeStockQuantity(product.getStockQuantity());
        productEntity.changeCreatedAt(product.getCreatedAt());
        return productEntity;
    }
}
