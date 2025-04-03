package com.musinsa.core.domain.brand.dao;

import com.musinsa.core.RepositoryTest;
import com.musinsa.core.domain.brand.entity.Brand;
import com.musinsa.core.domain.category.entity.Category;
import com.musinsa.core.domain.product.dao.ProductRepository;
import com.musinsa.core.domain.product.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@RepositoryTest
class BrandRepositoryTest {
    @Autowired
    BrandRepository brandRepository;
    @Autowired
    ProductRepository productRepository;

    @Test
    @DisplayName("브랜드에 속한 상품들이 함께 조회된다.")
    void findBySeqWithProducts() {
        //given
        Category testCategory1 = Category.builder()
                .name("카테고리1")
                .build();

        Brand testBrand = Brand.builder()
                .name("브랜드1")
                .build();

        Product product1 = Product.builder()
                .category(testCategory1)
                .brand(testBrand)
                .price(1000)
                .build();
        productRepository.save(product1);

        //when
        Optional<Brand> result = brandRepository.findBySeqWithProducts(testBrand.getSeq());

        //then
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get()).isEqualTo(testBrand);
    }
}