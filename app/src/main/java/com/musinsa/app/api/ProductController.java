package com.musinsa.app.api;

import com.musinsa.app.api.response.LowestProductsByCategoryResponse;
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

    @GetMapping("lowest-by-category")
    public ResponseEntity<LowestProductsByCategoryResponse> lowestByCategory() {
        List<ProductDto> lowestProductsByCategory = productService.getLowestProductsByCategory();
        long totalPrice = lowestProductsByCategory.stream().mapToLong(ProductDto::price).sum();

        return ResponseEntity.ok(LowestProductsByCategoryResponse.from(lowestProductsByCategory, totalPrice));
    }
}
