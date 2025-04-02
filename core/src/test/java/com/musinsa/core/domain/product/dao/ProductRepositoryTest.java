package com.musinsa.core.domain.product.dao;

import com.musinsa.core.RepositoryTest;
import com.musinsa.core.domain.brand.entity.Brand;
import com.musinsa.core.domain.category.entity.Category;
import com.musinsa.core.domain.product.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@RepositoryTest
public class ProductRepositoryTest {
    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("카테고리별 최저가 상품들이 조회되어야 한다.")
    public void getLowestProductsByCategory(){
        //given
        Category testCategory1 = Category.builder()
                .name("카테고리1")
                .build();
        Category testCategory2 = Category.builder()
                .name("카테고리2")
                .build();

        Brand testBrand = Brand.builder()
                .name("브랜드1")
                .build();

        Product product1 = Product.builder()
                .category(testCategory1)
                .brand(testBrand)
                .price(1000)
                .build();
        Product lowestProduct1 = Product.builder()
                .category(testCategory1)
                .brand(testBrand)
                .price(500)
                .build();
        Product product2 = Product.builder()
                .category(testCategory2)
                .brand(testBrand)
                .price(2000)
                .build();
        Product lowestProduct2 = Product.builder()
                .category(testCategory2)
                .brand(testBrand)
                .price(1000)
                .build();

        productRepository.saveAll(List.of(product1, product2,lowestProduct1,lowestProduct2));
        List<Product> lowestProducts = List.of(lowestProduct1, lowestProduct2);

        //when
        List<Product> lowestProductsByCategory = productRepository.getLowestProductsByCategory();

        //then
        assertThat(lowestProductsByCategory).hasSize(lowestProducts.size());
        assertThat(lowestProductsByCategory).hasSameElementsAs(lowestProducts);
    }
}