package abn.recipe.persistence.utils;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import static org.springframework.util.StringUtils.hasText;

@Component
public class SortUtils {

    public Sort getSort(String sortBy, String sortDirection) {
        var sort = Sort.unsorted();
        if (hasText(sortBy)) {
            var direction = "desc".equalsIgnoreCase(sortDirection)
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;
            sort = Sort.by(direction, sortBy);
        }
        return sort;
    }

}
