package abn.recipe.api.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PaginationMapperTest {

    private final PaginationMapper paginationMapper = new PaginationMapper();

    @Test
    void mapPaginationDto_whenPageHasContent_mapsCorrectly() {
        var pageable = PageRequest.of(0, 10);
        var pageImpl = new PageImpl<>(Collections.emptyList(), pageable, 20);

        var paginationDto = paginationMapper.mapPaginationDto(pageImpl);

        assertThat(paginationDto).isNotNull();
        assertThat(paginationDto.getMeta()).isNotNull();
        assertThat(paginationDto.getLinks()).isNotNull();

        assertThat(paginationDto.getMeta().getPage()).isZero();
        assertThat(paginationDto.getMeta().getPageSize()).isEqualTo(10);
        assertThat(paginationDto.getMeta().getTotalPages()).isEqualTo(2);
        assertThat(paginationDto.getMeta().getTotalItems()).isEqualTo(20);

        assertThat(paginationDto.getLinks().getSelf()).isEqualTo("page=0&page_size=10");
        assertThat(paginationDto.getLinks().getFirst()).isNull();
        assertThat(paginationDto.getLinks().getPrev()).isNull();
        assertThat(paginationDto.getLinks().getNext()).isEqualTo("page=1&page_size=10");
        assertThat(paginationDto.getLinks().getLast()).isEqualTo("page=1&page_size=10");
    }

    @Test
    void mapPaginationDto_whenPageIsFirstPage_mapsCorrectly() {
        var pageable = PageRequest.of(0, 10);
        var pageImpl = new PageImpl<>(Collections.emptyList(), pageable, 20);

        var paginationDto = paginationMapper.mapPaginationDto(pageImpl);

        assertThat(paginationDto).isNotNull();
        assertThat(paginationDto.getMeta()).isNotNull();
        assertThat(paginationDto.getLinks()).isNotNull();

        assertThat(paginationDto.getMeta().getPage()).isZero();
        assertThat(paginationDto.getMeta().getPageSize()).isEqualTo(10);
        assertThat(paginationDto.getMeta().getTotalPages()).isEqualTo(2);
        assertThat(paginationDto.getMeta().getTotalItems()).isEqualTo(20);

        assertThat(paginationDto.getLinks().getSelf()).isEqualTo("page=0&page_size=10");
        assertThat(paginationDto.getLinks().getNext()).isEqualTo("page=1&page_size=10");
        assertThat(paginationDto.getLinks().getLast()).isEqualTo("page=1&page_size=10");
    }

    @Test
    void mapPaginationDto_whenPageIsLastPage_mapsCorrectly() {
        var pageable = PageRequest.of(1, 10);
        var pageImpl = new PageImpl<>(Collections.emptyList(), pageable, 11);

        var paginationDto = paginationMapper.mapPaginationDto(pageImpl);

        assertThat(paginationDto).isNotNull();
        assertThat(paginationDto.getMeta()).isNotNull();
        assertThat(paginationDto.getLinks()).isNotNull();

        assertThat(paginationDto.getMeta().getPage()).isEqualTo(1);
        assertThat(paginationDto.getMeta().getPageSize()).isEqualTo(10);
        assertThat(paginationDto.getMeta().getTotalPages()).isEqualTo(2);
        assertThat(pageImpl.getTotalElements()).isEqualTo(11);

        assertThat(paginationDto.getLinks().getSelf()).isEqualTo("page=1&page_size=10");
        assertThat(paginationDto.getLinks().getFirst()).isEqualTo("page=0&page_size=10");
        assertThat(paginationDto.getLinks().getPrev()).isEqualTo("page=0&page_size=10");
    }

    @Test
    void mapPaginationDto_whenSinglePage_mapsCorrectly() {
        var pageable = PageRequest.of(0, 10);
        var pageImpl = new PageImpl<>(Collections.emptyList(), pageable, 5);

        var paginationDto = paginationMapper.mapPaginationDto(pageImpl);

        assertThat(paginationDto).isNotNull();
        assertThat(paginationDto.getMeta()).isNotNull();
        assertThat(paginationDto.getLinks()).isNotNull();

        assertThat(paginationDto.getMeta().getPage()).isZero();
        assertThat(paginationDto.getMeta().getPageSize()).isEqualTo(10);
        assertThat(paginationDto.getMeta().getTotalPages()).isEqualTo(1);
        assertThat(paginationDto.getMeta().getTotalItems()).isEqualTo(5);

        assertThat(paginationDto.getLinks().getSelf()).isEqualTo("page=0&page_size=10");
    }
}