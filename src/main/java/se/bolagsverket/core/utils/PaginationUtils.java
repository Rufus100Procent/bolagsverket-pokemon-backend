package se.bolagsverket.core.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public final class PaginationUtils {

    private PaginationUtils() {
    }

    public static Pageable createPageable(int page, int size, String sort, String order, String defaultSortField) {
        Sort.Direction direction = determineDirection(order);
        String sortField = (sort != null) ? sort : defaultSortField;
        return PageRequest.of(page, size, Sort.by(direction, sortField));
    }

    private static Sort.Direction determineDirection(String order) {
        return (order != null && order.equalsIgnoreCase("desc"))
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
    }

}