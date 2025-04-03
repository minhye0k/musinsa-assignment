package com.musinsa.app.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.musinsa.product.dto.LowestBrandProductsDto;
import com.musinsa.product.dto.ProductDto;
import lombok.Builder;

import java.util.List;

@Builder
public record LowestProductsByBrandResponse(@JsonProperty("최저가") LowestBrandProducts lowestBrandProducts) {

    public static LowestProductsByBrandResponse from(LowestBrandProductsDto lowestBrandProductsDto, long totalPrice) {
        LowestBrandProducts lowestBrandProducts = LowestBrandProducts.from(lowestBrandProductsDto.productDtos(), lowestBrandProductsDto.brand(), totalPrice);

        return LowestProductsByBrandResponse.builder()
                .lowestBrandProducts(lowestBrandProducts)
                .build();
    }

    @Builder
    private record LowestBrandProducts(@JsonProperty("카테고리") List<Product> products,
                                       @JsonProperty("브랜드") String brand,
                                       @JsonProperty("총액") long totalPrice) {
        public static LowestBrandProducts from(List<ProductDto> productDtos, String brand, long totalPrice) {
            List<Product> products = productDtos.stream().map(Product::from).toList();

            return LowestBrandProducts.builder()
                    .products(products)
                    .brand(brand)
                    .totalPrice(totalPrice)
                    .build();
        }
    }

    @Builder
    private record Product(@JsonProperty("카테고리") String category,
                   @JsonProperty("가격") long price) {

        public static Product from(ProductDto productDto) {
            return Product.builder()
                    .category(productDto.category())
                    .price(productDto.price())
                    .build();
        }
    }
}
