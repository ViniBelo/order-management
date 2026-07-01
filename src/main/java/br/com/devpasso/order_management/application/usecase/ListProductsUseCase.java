package br.com.devpasso.order_management.application.usecase;

import br.com.devpasso.order_management.domain.common.PaginatedResult;
import br.com.devpasso.order_management.domain.common.PaginationQuery;
import br.com.devpasso.order_management.domain.model.Product;

public interface ListProductsUseCase {
    PaginatedResult<Product> execute(PaginationQuery paginationQuery, String name);
}
