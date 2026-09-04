package br.com.devpasso.order_management.infrastructure.persistence.adapter.mapper;

import br.com.devpasso.order_management.domain.common.PaginatedQueryResult;
import br.com.devpasso.order_management.domain.common.PaginationQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class InfraPaginationMapper {

    /**
     * Converts the Domain Query into Spring Data's PageRequest.
     */
    public Pageable toSpringPageable(PaginationQuery query) {
        Sort sort = parseSort(query.sort());
        return PageRequest.of(query.page(), query.size(), sort);
    }

    /**
     * Converts the Spring Data Page into the Domain PaginatedQueryResult.
     */
    public <T> PaginatedQueryResult<T> toDomainResult(Page<?> springPage, List<T> mappedContent) {
        return new PaginatedQueryResult<>(
                mappedContent,
                springPage.getNumber(),
                springPage.getSize(),
                springPage.getTotalElements(),
                springPage.getTotalPages()
        );
    }

    /**
     * Internal helper to transform the "name,ASC" string back into a Spring Sort object.
     */
    private Sort parseSort(String sortParam) {
        if (sortParam == null || sortParam.isBlank()) {
            return Sort.unsorted();
        }

        List<Sort.Order> orders = Arrays.stream(sortParam.split(";"))
                .map(String::trim)
                .filter(sort -> !sort.isBlank())
                .map(sort -> {
                    String[] parts = sort.split(",");

                    String property = parts[0].trim();
                    Sort.Direction direction = Sort.Direction.ASC;

                    if (parts.length > 1 && parts[1].trim().equalsIgnoreCase("DESC")) {
                        direction = Sort.Direction.DESC;
                    }

                    return new Sort.Order(direction, property);
                })
                .toList();

        return Sort.by(orders);
    }
}
