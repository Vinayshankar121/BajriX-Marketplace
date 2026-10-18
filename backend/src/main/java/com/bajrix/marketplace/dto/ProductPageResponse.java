package com.bajrix.marketplace.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public record ProductPageResponse<T>(
        List<T> content,
        int number,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {
    public static <T> ProductPageResponse<T> from(Page<T> page) {
        return new ProductPageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
}
