package com.musinsa.core.domain.product.dto;

import lombok.Builder;

@Builder
public record ProductQueryParam(String categoryName) {
    private static final ProductQueryParam EMPTY = ProductQueryParam.builder().build();

    public static ProductQueryParam empty() {
        return EMPTY;
    }
}
