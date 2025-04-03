package com.musinsa.app.api;

import com.musinsa.app.api.response.LowestProductsByBrandResponse;
import com.musinsa.app.api.response.LowestProductsByCategoryResponse;
import com.musinsa.app.api.response.MostProductsByCategoryResponse;
import com.musinsa.product.ProductService;
import com.musinsa.product.dto.LowestBrandProductsDto;
import com.musinsa.product.dto.ProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("lowest-by-brand")
    public ResponseEntity<LowestProductsByBrandResponse> lowestByBrand() {
        LowestBrandProductsDto lowestProductsByBrand = productService.getLowestProductsByBrand();
        long totalPrice = lowestProductsByBrand.productDtos().stream().mapToLong(ProductDto::price).sum();

        return ResponseEntity.ok(LowestProductsByBrandResponse.from(lowestProductsByBrand, totalPrice));
    }

    @GetMapping("most-by-category")
    public ResponseEntity<MostProductsByCategoryResponse> mostByCategory(@RequestParam("category") String category) {
        List<ProductDto> lowestProductsByCategory = productService.getLowestProductsByCategory(category);
        List<ProductDto> highestProductsByCategory = productService.getHighestProductsByCategory(category);

        return ResponseEntity.ok(MostProductsByCategoryResponse.from(category, lowestProductsByCategory, highestProductsByCategory));
    }
}
