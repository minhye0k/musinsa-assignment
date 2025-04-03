package com.musinsa.app.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.musinsa.product.dto.ProductDto;
import lombok.Builder;

import java.util.List;

@Builder
public record MostProductsByCategoryResponse(@JsonProperty("카테고리") String category,
                                             @JsonProperty("최저가") List<Product> lowestProducts,
                                             @JsonProperty("최고가") List<Product> highestProducts) {
    public static MostProductsByCategoryResponse from(String category,
                                                      List<ProductDto> lowestProductDtos,
                                                      List<ProductDto> highestProductDtos) {
        List<Product> lowestProduct = lowestProductDtos.stream().map(Product::from).toList();
        List<Product> highestProduct = highestProductDtos.stream().map(Product::from).toList();

        return MostProductsByCategoryResponse.builder()
                .category(category)
                .lowestProducts(lowestProduct)
                .highestProducts(highestProduct)
                .build();
    }

    @Builder
    private record Product(@JsonProperty("브랜드") String brand,
                           @JsonProperty("가격") long price) {

        public static Product from(ProductDto productDto) {
            return Product.builder()
                    .brand(productDto.brand())
                    .price(productDto.price())
                    .build();
        }
    }
}
