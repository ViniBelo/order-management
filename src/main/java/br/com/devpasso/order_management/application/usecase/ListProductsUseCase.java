package br.com.devpasso.order_management.application.usecase;

import br.com.devpasso.order_management.domain.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListProductsUseCase {
    Page<Product> execute(Pageable pageable);
}
