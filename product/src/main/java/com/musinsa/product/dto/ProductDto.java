package com.musinsa.product.dto;

import com.musinsa.core.domain.product.entity.Product;
import lombok.Builder;

@Builder
public record ProductDto(String category, String brand, long price) {

    public static ProductDto from(Product product) {
        return ProductDto.builder()
                .category(product.getCategory().getName())
                .brand(product.getBrand().getName())
                .price(product.getPrice())
                .build();
    }
}
