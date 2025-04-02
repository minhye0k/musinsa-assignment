package com.musinsa.product;

import com.musinsa.core.domain.brand.entity.Brand;
import com.musinsa.core.domain.category.entity.Category;
import com.musinsa.core.domain.product.dao.ProductRepository;
import com.musinsa.core.domain.product.entity.Product;
import com.musinsa.product.dto.ProductDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @InjectMocks
    private ProductService productService;
    @Mock
    private ProductRepository productRepository;

    @Test
    @DisplayName("카테고리 별 최저가 상품 목록이 반환되어야 한다.")
    void getLowestProductsByCategory() {
        //given
        Category testCategory1 = Category.builder()
                .seq(1L)
                .name("카테고리1")
                .build();
        Category testCategory2 = Category.builder()
                .seq(2L)
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
        Product product2 = Product.builder()
                .category(testCategory2)
                .brand(testBrand)
                .price(2000)
                .build();
        List<Product> products = List.of(product1, product2);
        given(productRepository.getLowestProductsByCategory()).willReturn(products);

        List<ProductDto> productDtos = products.stream().map(ProductDto::from).toList();

        //when
        List<ProductDto> result = productService.getLowestProductsByCategory();

        //then
        assertThat(result).hasSize(productDtos.size());
        assertThat(result).hasSameElementsAs(productDtos);
    }

    @Test
    @DisplayName("카테고리별 최저가가 같은 상품이 존재하더라도 하나만 나와야 한다.")
    public void getLowestProductsByCategoryNoDuplication(){
        //given
        Category testCategory1 = Category.builder()
                .seq(1L)
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
        Product lowestProduct1 = Product.builder()
                .category(testCategory1)
                .brand(testBrand)
                .price(1000)
                .build();

        List<Product> products = List.of(product1, lowestProduct1);
        given(productRepository.getLowestProductsByCategory()).willReturn(products);
        List<ProductDto> productDtos = products.stream().map(ProductDto::from).toList();

        //when
        List<ProductDto> result = productService.getLowestProductsByCategory();

        //then
        assertThat(result).hasSize(1);
        assertThat(result).hasSameElementsAs(productDtos);
    }
}