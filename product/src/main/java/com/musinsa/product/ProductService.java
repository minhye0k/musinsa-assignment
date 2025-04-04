package com.musinsa.product;

import com.musinsa.common.config.CacheType;
import com.musinsa.common.exception.CustomException;
import com.musinsa.core.domain.brand.dao.BrandRepository;
import com.musinsa.core.domain.brand.entity.Brand;
import com.musinsa.core.domain.category.dao.CategoryRepository;
import com.musinsa.core.domain.category.entity.Category;
import com.musinsa.core.domain.product.dao.ProductRepository;
import com.musinsa.core.domain.product.dto.ProductQueryParam;
import com.musinsa.core.domain.product.entity.Product;
import com.musinsa.product.dto.BrandAndCategoryKey;
import com.musinsa.product.dto.LowestBrandProductsDto;
import com.musinsa.product.dto.ProductDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.repository.query.Param;
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
    private final BrandRepository brandRepository;

    @Cacheable(cacheNames = "lowestProductsByCategory")
    public List<ProductDto> getLowestProductsByCategory() {

        List<Product> products = productRepository.getLowestProductsByCategory(ProductQueryParam.empty());

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

    @Cacheable(cacheNames = "lowestProductsByBrand")
    public LowestBrandProductsDto getLowestProductsByBrand() {
        List<Product> products = productRepository.getLowestProductsByBrandAndCategory();

        List<Product> dedupedProducts = dedupeByBrandAndCategory(products);
        Long lowestBrandSeq = getLowestBrandSeq(dedupedProducts);

        List<ProductDto> lowestProducts = dedupedProducts.stream()
                .filter(p -> p.getBrand().getSeq().equals(lowestBrandSeq))
                .sorted(Comparator.comparing(p -> p.getCategory().getSeq()))
                .map(ProductDto::from)
                .toList();

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
    private Long getLowestBrandSeq(List<Product> products) {
        Map<Long, List<Product>> productsByBrandSeq = products.stream()
                .collect(Collectors.groupingBy(p -> p.getBrand().getSeq()));

        long categoryCount = categoryRepository.count();

        long lowestTotalPrice = Long.MAX_VALUE;
        Long lowestBrandSeq = 0L;
        for (Map.Entry<Long, List<Product>> productEntry : productsByBrandSeq.entrySet()) {
            List<Product> productsByBrand = productEntry.getValue();
            // 모든 카테고리별로 상폼이 하나라도 존재하지 않으면 최저가 브랜드 후보에서 제외된다.
            if (productsByBrand.size() != categoryCount) continue;

            long totalPrice = productsByBrand.stream().mapToLong(Product::getPrice).sum();

            if (totalPrice < lowestTotalPrice) {
                lowestTotalPrice = totalPrice;
                lowestBrandSeq = productEntry.getKey();
            }
        }

        if (lowestBrandSeq == 0) throw CustomException.notFound("최저가 브랜드를 찾을 수 없습니다.");

        return lowestBrandSeq;
    }

    @Cacheable(cacheNames = "lowestProduct", key = "#category")
    public List<ProductDto> getLowestProductsByCategory(String category) {
        boolean categoryExist = categoryRepository.existsByName(category);
        if (!categoryExist) throw CustomException.notFound("해당 카테고리를 찾을 수 없습니다.");

        ProductQueryParam productQueryParam = ProductQueryParam.builder()
                .categoryName(category)
                .build();
        List<Product> lowestProductsByCategory = productRepository.getLowestProductsByCategory(productQueryParam);

        return lowestProductsByCategory.stream().map(ProductDto::from).toList();

    }

    @Cacheable(cacheNames = "highestProduct", key = "#category")
    public List<ProductDto> getHighestProductsByCategory(String category) {
        boolean categoryExist = categoryRepository.existsByName(category);
        if (!categoryExist) throw CustomException.notFound("해당 카테고리를 찾을 수 없습니다.");

        ProductQueryParam productQueryParam = ProductQueryParam.builder()
                .categoryName(category)
                .build();
        List<Product> highestProductsByCategory = productRepository.getHighestProductsByCategory(productQueryParam);

        return highestProductsByCategory.stream().map(ProductDto::from).toList();
    }

    @Caching(
            evict = {
                    @CacheEvict(cacheNames = {"lowestProductsByCategory", "lowestProductsByBrand"}),
                    @CacheEvict(cacheNames = {"lowestProduct", "highestProduct"}, key = "#productDto.category")
            }
    )
    @Transactional
    public Long create(ProductDto productDto) {
        String brandName = productDto.brand();
        Brand brand = brandRepository.findByName(brandName)
                .orElseThrow(() -> CustomException.notFound("지정하신 브랜드를 찾을 수 없습니다."));

        String categoryName = productDto.category();
        Category category = categoryRepository.findByName(categoryName)
                .orElseThrow(() -> CustomException.notFound("지정하신 카테고리를 찾을 수 없습니다."));

        Product product = Product.builder()
                .brand(brand)
                .price(productDto.price())
                .category(category)
                .build();

        Product savedProduct = productRepository.save(product);
        return savedProduct.getSeq();
    }

    @Caching(
            evict = {
                    @CacheEvict(cacheNames = {"lowestProductsByCategory", "lowestProductsByBrand"}),
                    @CacheEvict(cacheNames = {"lowestProduct", "highestProduct"}, key = "#productDto.category")
            }
    )
    @Transactional
    public Long update(Long seq, ProductDto productDto) {
        Product product = productRepository.findById(seq)
                .orElseThrow(() -> CustomException.notFound("해당 상품을 찾을 수 없습니다."));

        String brandName = productDto.brand();
        Brand brand = brandRepository.findByName(brandName)
                .orElseThrow(() -> CustomException.notFound("지정하신 브랜드를 찾을 수 없습니다."));

        String categoryName = productDto.category();
        Category category = categoryRepository.findByName(categoryName)
                .orElseThrow(() -> CustomException.notFound("지정하신 카테고리를 찾을 수 없습니다."));

        product.update(brand, category, productDto.price());
        return product.getSeq();
    }

    @CacheEvict(cacheNames = {"lowestProductsByCategory", "lowestProductsByBrand", "lowestProduct", "highestProduct"}, allEntries = true)
    @Transactional
    public Long delete(Long seq) {
        Product product = productRepository.findById(seq)
                .orElseThrow(() -> CustomException.notFound("해당 상품을 찾을 수 없습니다."));

        productRepository.delete(product);
        return product.getSeq();
    }
}
