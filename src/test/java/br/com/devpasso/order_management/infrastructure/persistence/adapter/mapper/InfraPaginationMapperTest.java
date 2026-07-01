package br.com.devpasso.order_management.infrastructure.persistence.adapter.mapper;

import br.com.devpasso.order_management.domain.common.PaginatedResult;
import br.com.devpasso.order_management.domain.common.PaginationQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

public class InfraPaginationMapperTest {
    private InfraPaginationMapper mapper;

    private PaginationQuery paginationQuery;

    @BeforeEach
    void setUp() {
        mapper = new InfraPaginationMapper();
        paginationQuery = new PaginationQuery(0, 10, "name,ASC");
    }

    @Test
    @DisplayName("Should return unsorted Pageable when sort parameter is null")
    void toSpringPageable_NullSort_ReturnsUnsorted() {
        PaginationQuery paginationQuery = new PaginationQuery(0, 10, null);
        Pageable result = mapper.toSpringPageable(paginationQuery);

        assertThat(result.getPageNumber()).isEqualTo(0);
        assertThat(result.getPageSize()).isEqualTo(10);
        assertThat(result.getSort().isSorted()).isFalse();
    }

    @Test
    @DisplayName("Should return unsorted Pageable when sort parameter is blank")
    void toSpringPageable_BlankSort_ReturnsUnsorted() {
        PaginationQuery paginationQuery = new PaginationQuery(0, 10, "   ");

        Pageable result = mapper.toSpringPageable(paginationQuery);

        assertThat(result.getSort().isSorted()).isFalse();
    }

    @Test
    @DisplayName("Should default to ASC direction when direction is omitted")
    void toSpringPageable_OmittedDirection_DefaultsToAsc() {
        Pageable result = mapper.toSpringPageable(paginationQuery);

        Sort sort = result.getSort();
        assertThat(sort.isSorted()).isTrue();
        assertThat(sort.getOrderFor("name")).isNotNull();
        assertThat(Objects.requireNonNull(sort.getOrderFor("name")).getDirection()).isEqualTo(Sort.Direction.ASC);
    }

    @Test
    @DisplayName("Should parse DESC direction correctly")
    void toSpringPageable_DescDirection_ParsesCorrectly() {
        paginationQuery = new PaginationQuery(0, 10, "name,DESC");
        Pageable result = mapper.toSpringPageable(paginationQuery);

        Sort sort = result.getSort();
        assertThat(sort.getOrderFor("name")).isNotNull();
        assertThat(Objects.requireNonNull(sort.getOrderFor("name")).getDirection()).isEqualTo(Sort.Direction.DESC);
    }

    @Test
    @DisplayName("Should correctly map Spring Page properties to Domain PaginatedResult")
    void toDomainResult_ValidSpringPage_MapsCorrectly() {
        Page<?> springPage = new PageImpl<>(List.of("Item 1", "Item 2"), PageRequest.of(2, 20), 150L);

        List<String> mappedContent = List.of("Item 1", "Item 2");

        PaginatedResult<String> result = mapper.toDomainResult(springPage, mappedContent);

        assertThat(result).isNotNull();
        assertThat(result.content()).isEqualTo(mappedContent);
        assertThat(result.page()).isEqualTo(2);
        assertThat(result.size()).isEqualTo(20);
        assertThat(result.totalElements()).isEqualTo(150L);
        assertThat(result.totalPages()).isEqualTo(8);
    }
}
