package br.com.devpasso.order_management.domain.repository;

import br.com.devpasso.order_management.infrastructure.persistence.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepository {
    Page<Product> listAll(Pageable pageable, String name);
}
