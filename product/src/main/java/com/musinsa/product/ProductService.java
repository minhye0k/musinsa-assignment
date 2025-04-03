package com.musinsa.product;

import com.musinsa.core.domain.brand.entity.Brand;
import com.musinsa.core.domain.category.dao.CategoryRepository;
import com.musinsa.core.domain.category.entity.Category;
import com.musinsa.core.domain.product.dao.ProductRepository;
import com.musinsa.core.domain.product.entity.Product;
import com.musinsa.product.dto.BrandAndCategoryKey;
import com.musinsa.product.dto.LowestBrandProductsDto;
import com.musinsa.product.dto.ProductDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public List<ProductDto> getLowestProductsByCategory() {
        List<Product> products = productRepository.getLowestProductsByCategory();

        List<Product> dedupedProducts = dedupeByCategory(products);
        return dedupedProducts.stream().map(ProductDto::from).toList();
    }

    private List<Product> dedupeByCategory(List<Product> products) {
        Map<Long, Product> productByCategory = new HashMap<>();

        for (Product product : products) {
            // 카테고리 정보 없으면 노출 x, 로그 및 메신저 알람으로 감지 및 대응
            Category category = product.getCategory();
            if (category == null) {
                log.info("product (seq : {}) have no category", product.getSeq());
                continue;
            }

            productByCategory.putIfAbsent(category.getSeq(), product);
        }
        return productByCategory.values().stream().toList();
    }


    public LowestBrandProductsDto getLowestProductsByBrand() {
        List<Product> products = productRepository.getLowestProductsByBrandAndCategory();

        List<Product> dedupedProducts = dedupeByBrandAndCategory(products);
        Long lowestBrandSeq = getLowestBrandSeq(dedupedProducts);

        List<ProductDto> lowestProducts = dedupedProducts.stream().filter(p-> p.getBrand().getSeq().equals(lowestBrandSeq)).map(ProductDto::from).toList();
        String brand = lowestProducts.isEmpty() ? "" : lowestProducts.get(0).brand();
        return LowestBrandProductsDto.builder()
                .productDtos(lowestProducts)
                .brand(brand)
                .build();
    }

    private List<Product> dedupeByBrandAndCategory(List<Product> products) {
        Map<BrandAndCategoryKey, Product> productByBrandAndCategory = new HashMap<>();

        for (Product product : products) {
            Category category = product.getCategory();
            Brand brand = product.getBrand();

            // 카테고리나 브랜드 정보 없으면 노출 x, 로그 및 메신저 알람으로 감지 및 대응
            if (category == null || brand == null) {
                log.info("product (seq : {}) have no brand or category", product.getSeq());
                continue;
            }

            BrandAndCategoryKey brandAndCategoryKey = BrandAndCategoryKey.builder()
                    .categorySeq(category.getSeq())
                    .brandSeq(brand.getSeq())
                    .build();
            productByBrandAndCategory.putIfAbsent(brandAndCategoryKey, product);
        }
        return productByBrandAndCategory.values().stream().toList();
    }

    // 브랜드 별로 집계 해서 합계 구한 다음 최댓값 가진 브랜드랑 거기에 딸린 상품들 가져옴
    private Long getLowestBrandSeq(List<Product> products){
        Map<Long, List<Product>> productsByBrandSeq = products.stream()
                .collect(Collectors.groupingBy(p -> p.getBrand().getSeq()));

        long categoryCount = categoryRepository.count();

        long lowestTotalPrice = Long.MAX_VALUE;
        Long lowestBrandSeq = 0L;
        for (Map.Entry<Long, List<Product>> productEntry : productsByBrandSeq.entrySet()) {
            List<Product> productsByBrand = productEntry.getValue();
            // 모든 카테고리별로 상폼이 하나라도 존재하지 않으면 최저가 브랜드 후보에서 제외된다.
            if(productsByBrand.size() != categoryCount) continue;

            long totalPrice = productsByBrand.stream().mapToLong(Product::getPrice).sum();

            if (totalPrice < lowestTotalPrice) {
                lowestTotalPrice = totalPrice;
                lowestBrandSeq = productEntry.getKey();
            }
        }

        if(lowestBrandSeq == 0) throw new RuntimeException("lowest brand not found");

        return lowestBrandSeq;
    }

}
