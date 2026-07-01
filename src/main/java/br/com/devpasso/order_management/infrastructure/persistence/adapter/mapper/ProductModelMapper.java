package br.com.devpasso.order_management.infrastructure.persistence.adapter.mapper;

import br.com.devpasso.order_management.domain.model.Product;
import br.com.devpasso.order_management.infrastructure.persistence.entity.ProductEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductModelMapper {
    public Product toModel (ProductEntity productEntity) {
        return new Product(
                productEntity.getId(),
                productEntity.getName(),
                productEntity.getDescription(),
                productEntity.getPrice(),
                productEntity.getStockQuantity(),
                productEntity.getCreatedAt()
        );
    }
}
