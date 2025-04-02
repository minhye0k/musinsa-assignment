package com.musinsa.app.api.response;

import com.musinsa.product.dto.ProductDto;
import lombok.Builder;

import java.util.List;

@Builder
public record LowestProductsResponse(List<Product> products, long totalPrice) {

    public static LowestProductsResponse from(List<ProductDto> productDtos, long totalPrice) {
        List<Product> products = productDtos.stream().map(Product::from).toList();

        return LowestProductsResponse.builder()
                .products(products)
                .totalPrice(totalPrice)
                .build();
    }

    @Builder
    record Product(String category, String brand, long price) {

        public static Product from(ProductDto productDto) {
            return Product.builder()
                    .category(productDto.category())
                    .brand(productDto.brand())
                    .price(productDto.price())
                    .build();
        }
    }
}
