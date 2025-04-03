package com.musinsa.product.dto;

import com.musinsa.core.domain.brand.entity.Brand;
import com.musinsa.core.domain.category.entity.Category;
import com.musinsa.core.domain.product.entity.Product;
import lombok.Builder;

@Builder
public record ProductDto(String category, String brand, long price) {

    public static ProductDto from(Product product) {
        Category category = product.getCategory();
        String categoryName = category != null ? category.getName() : null;

        Brand brand = product.getBrand();
        String brandName = brand != null ? brand.getName() : null;


        return ProductDto.builder()
                .category(categoryName)
                .brand(brandName)
                .price(product.getPrice())
                .build();
    }
}
