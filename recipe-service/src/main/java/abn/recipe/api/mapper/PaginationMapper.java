package abn.recipe.api.mapper;

import abn.recipe.spec.spec.PaginationDto;
import abn.recipe.spec.spec.PaginationLinksDto;
import abn.recipe.spec.spec.PaginationMetaDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class PaginationMapper {
    private static final String PAGINATION_PAGE = "page=";
    private static final String PAGINATION_SIZE = "&page_size=";

    public PaginationDto mapPaginationDto(Page<?> page) {
        var pagination = new PaginationDto();
        pagination.setMeta(mapPaginationMeta(page));
        pagination.setLinks(mapPaginationLinks(page));
        return pagination;
    }

    private PaginationMetaDto mapPaginationMeta(Page<?> page) {
        var paginationMeta = new PaginationMetaDto();
        paginationMeta.setPage(page.getNumber());
        paginationMeta.setPageSize(page.getSize());
        paginationMeta.setPageType(PaginationMetaDto.PageTypeEnum.PAGE);
        paginationMeta.setTotalPages(page.getTotalPages());
        paginationMeta.setTotalItems(page.getTotalElements());
        return paginationMeta;
    }

    private PaginationLinksDto mapPaginationLinks(Page<?> page) {
        var paginationLinks = new PaginationLinksDto();
        var self = page.getPageable();
        paginationLinks.setSelf(PAGINATION_PAGE + self.getPageNumber() + PAGINATION_SIZE + self.getPageSize());
        if (page.hasPrevious()) {
            var first = self.first();
            var prev = self.previousOrFirst();
            paginationLinks.setFirst(PAGINATION_PAGE + first.getPageNumber() + PAGINATION_SIZE + first.getPageSize());
            paginationLinks.setPrev(PAGINATION_PAGE + prev.getPageNumber() + PAGINATION_SIZE + prev.getPageSize());
        }
        if (page.hasNext()) {
            var next = self.next();
            paginationLinks.setNext(PAGINATION_PAGE + next.getPageNumber() + PAGINATION_SIZE + next.getPageSize());
            paginationLinks.setLast(PAGINATION_PAGE + (page.getTotalPages() - 1) + PAGINATION_SIZE + page.getSize());
        }
        return paginationLinks;
    }

}
