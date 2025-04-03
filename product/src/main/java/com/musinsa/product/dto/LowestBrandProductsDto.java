package com.musinsa.product.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record LowestBrandProductsDto(List<ProductDto> productDtos,
                                     String brand) {
}
