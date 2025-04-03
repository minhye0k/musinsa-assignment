package com.musinsa.product.dto;

import lombok.Builder;

@Builder
public record BrandAndCategoryKey(Long brandSeq, Long categorySeq) {
}
