package com.musinsa.app.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.musinsa.product.dto.ProductDto;
import lombok.Builder;

import java.util.List;

@Builder
public record LowestProductsByCategoryResponse(@JsonProperty("최저가") List<Product> products,
                                               @JsonProperty("총액") long totalPrice) {

    public static LowestProductsByCategoryResponse from(List<ProductDto> productDtos, long totalPrice) {
        List<Product> products = productDtos.stream().map(Product::from).toList();

        return LowestProductsByCategoryResponse.builder()
                .products(products)
                .totalPrice(totalPrice)
                .build();
    }

    @Builder
    record Product(@JsonProperty("카테고리") String category,
                   @JsonProperty("브랜드") String brand,
                   @JsonProperty("가격") long price) {

        public static Product from(ProductDto productDto) {
            return Product.builder()
                    .category(productDto.category())
                    .brand(productDto.brand())
                    .price(productDto.price())
                    .build();
        }
    }
}
