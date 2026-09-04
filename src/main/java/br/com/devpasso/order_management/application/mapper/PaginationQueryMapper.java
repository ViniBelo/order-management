package br.com.devpasso.order_management.application.mapper;

import br.com.devpasso.order_management.domain.common.PaginationQuery;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class PaginationQueryMapper {

    /**
     * Translates the Spring Web `Pageable` into the Domain’s plain Query object.
     */
    public PaginationQuery toDomainQuery(Pageable pageable) {
        if (pageable.isUnpaged()) {
            return new PaginationQuery(0, Integer.MAX_VALUE, "");
        }

        String sortParam = "";
        if (pageable.getSort().isSorted()) {
            sortParam = pageable.getSort().stream()
                    .map(order -> order.getProperty() + "," + order.getDirection().name())
                    .collect(Collectors.joining(";"));
        }

        return new PaginationQuery(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sortParam
        );
    }
}
