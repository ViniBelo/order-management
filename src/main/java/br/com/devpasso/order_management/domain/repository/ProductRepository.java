package br.com.devpasso.order_management.domain.repository;

import br.com.devpasso.order_management.infrastructure.persistence.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepository {
    Page<ProductEntity> findAllByNameContainingIgnoreCase(Pageable pageable, String name);
}
