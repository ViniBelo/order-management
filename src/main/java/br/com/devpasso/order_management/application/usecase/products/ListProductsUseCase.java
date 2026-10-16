package br.com.devpasso.order_management.application.usecase.products;

import br.com.devpasso.order_management.application.dto.result.PaginatedResult;
import br.com.devpasso.order_management.application.dto.result.products.ProductResult;
import br.com.devpasso.order_management.domain.common.PaginationQuery;

public interface ListProductsUseCase {
    PaginatedResult<ProductResult> execute(PaginationQuery paginationQuery, String name);
}
