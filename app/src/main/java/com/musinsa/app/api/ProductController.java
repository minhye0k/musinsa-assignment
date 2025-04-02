package com.musinsa.app.api;

import com.musinsa.app.api.response.LowestProductsResponse;
import com.musinsa.product.ProductService;
import com.musinsa.product.dto.ProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("lowest")
    public ResponseEntity<LowestProductsResponse> lowest() {
        List<ProductDto> lowestProductsByCategory = productService.getLowestProductsByCategory();
        long totalPrice = lowestProductsByCategory.stream().mapToLong(ProductDto::price).sum();

        return ResponseEntity.ok(LowestProductsResponse.from(lowestProductsByCategory, totalPrice));
    }
}
