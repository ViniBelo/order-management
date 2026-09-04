package br.com.devpasso.order_management.api.mapper.response;

import br.com.devpasso.order_management.api.dto.response.PaginatedResponse;
import br.com.devpasso.order_management.application.dto.result.PaginatedResult;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PaginatedResponseMapper {
    public <T, Y> PaginatedResponse<T> from(PaginatedResult<Y> paginatedResult, List<T> mappedContent) {
        return new PaginatedResponse<>(
                mappedContent,
                paginatedResult.page(),
                paginatedResult.size(),
                paginatedResult.totalElements(),
                paginatedResult.totalPages()
        );
    }
}
