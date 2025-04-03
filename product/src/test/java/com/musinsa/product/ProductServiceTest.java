package com.musinsa.product;

import com.musinsa.core.domain.brand.entity.Brand;
import com.musinsa.core.domain.category.dao.CategoryRepository;
import com.musinsa.core.domain.category.entity.Category;
import com.musinsa.core.domain.product.dao.ProductRepository;
import com.musinsa.core.domain.product.dto.ProductQueryParam;
import com.musinsa.core.domain.product.entity.Product;
import com.musinsa.product.dto.LowestBrandProductsDto;
import com.musinsa.product.dto.ProductDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @InjectMocks
    private ProductService productService;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryRepository categoryRepository;

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
        given(productRepository.getLowestProductsByCategory(ProductQueryParam.empty())).willReturn(products);

        List<ProductDto> productDtos = products.stream().map(ProductDto::from).toList();

        //when
        List<ProductDto> result = productService.getLowestProductsByCategory();

        //then
        assertThat(result).hasSize(productDtos.size());
        assertThat(result).hasSameElementsAs(productDtos);
    }

    @Test
    @DisplayName("카테고리별 최저가가 같은 상품이 존재하더라도 하나만 나와야 한다.")
    public void getLowestProductsByCategoryNoDuplication() {
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
        given(productRepository.getLowestProductsByCategory(ProductQueryParam.empty())).willReturn(products);
        List<ProductDto> productDtos = products.stream().map(ProductDto::from).toList();

        //when
        List<ProductDto> result = productService.getLowestProductsByCategory();

        //then
        assertThat(result).hasSize(1);
        assertThat(result).hasSameElementsAs(productDtos);
    }

    @Test
    @DisplayName("모든 카테고리별 상품의 합이 최저가인 브랜드와 상품 목록이 나와야 한다.")
    public void getLowestProductsByBrand() {
        //given
        Category testCategory1 = Category.builder()
                .seq(1L)
                .name("카테고리1")
                .build();
        Category testCategory2 = Category.builder()
                .seq(2L)
                .name("카테고리2")
                .build();

        Brand testBrand1 = Brand.builder()
                .seq(1L)
                .name("브랜드1")
                .build();
        Brand testBrand2 = Brand.builder()
                .seq(2L)
                .name("브랜드2")
                .build();

        Product lowestBrandProduct1 = Product.builder()
                .category(testCategory1)
                .brand(testBrand1)
                .price(1000)
                .build();
        Product lowestBrandProduct2 = Product.builder()
                .category(testCategory2)
                .brand(testBrand1)
                .price(1000)
                .build();
        Product product3 = Product.builder()
                .category(testCategory1)
                .brand(testBrand2)
                .price(2000)
                .build();
        Product product4 = Product.builder()
                .category(testCategory2)
                .brand(testBrand2)
                .price(2000)
                .build();

        List<Product> products = List.of(lowestBrandProduct1, lowestBrandProduct2, product3, product4);
        given(productRepository.getLowestProductsByBrandAndCategory()).willReturn(products);
        given(categoryRepository.count()).willReturn(2L);

        List<Product> lowestBrandProducts = List.of(lowestBrandProduct1, lowestBrandProduct2);
        List<ProductDto> productDtos = lowestBrandProducts.stream().map(ProductDto::from).toList();
        LowestBrandProductsDto lowestBrandProductsDto = LowestBrandProductsDto.builder()
                .brand(testBrand1.getName())
                .productDtos(productDtos)
                .build();

        //when
        LowestBrandProductsDto result = productService.getLowestProductsByBrand();

        //then
        assertThat(lowestBrandProductsDto).isEqualTo(result);
        assertThat(result.productDtos()).hasSameElementsAs(productDtos);
    }

    @Test
    @DisplayName("브랜드 별 동일 카테고리의 최저가 상품이 여러개라도 하나만 나와야 한다.")
    public void getLowestProductsByBrandNoDuplication() {
        //given
        Category testCategory1 = Category.builder()
                .seq(1L)
                .name("카테고리1")
                .build();

        Brand testBrand1 = Brand.builder()
                .seq(1L)
                .name("브랜드1")
                .build();

        Product lowestBrandProduct1 = Product.builder()
                .category(testCategory1)
                .brand(testBrand1)
                .price(1000)
                .build();
        Product lowestBrandProduct2 = Product.builder()
                .category(testCategory1)
                .brand(testBrand1)
                .price(1000)
                .build();

        List<Product> products = List.of(lowestBrandProduct1, lowestBrandProduct2);
        given(productRepository.getLowestProductsByBrandAndCategory()).willReturn(products);
        given(categoryRepository.count()).willReturn(1L);

        //when
        LowestBrandProductsDto result = productService.getLowestProductsByBrand();

        //then
        assertThat(result.productDtos()).hasSize(1);
    }

    @Test
    @DisplayName("모든 카테고리의 상품을 갖고 있지 않으면 제외된다.")
    public void getLowestProductsByBrandHaveNoAllCategoryException() {
        //given
        Category testCategory1 = Category.builder()
                .seq(1L)
                .name("카테고리1")
                .build();

        Brand testBrand1 = Brand.builder()
                .seq(1L)
                .name("브랜드1")
                .build();

        Product product1 = Product.builder()
                .category(testCategory1)
                .brand(testBrand1)
                .price(1000)
                .build();

        List<Product> products = List.of(product1);
        given(productRepository.getLowestProductsByBrandAndCategory()).willReturn(products);
        given(categoryRepository.count()).willReturn(2L);

        //when & then
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> productService.getLowestProductsByBrand())
                .withMessage("lowest brand not found");
    }

    @Test
    @DisplayName("요청받은 카테고리명에 해당하는 최저가 상품들이 조회되어야 한다.")
    void getLowestProductsByCategoryName() {
        //given
        String requestedCategoryName = "카테고리1";
        Category testCategory1 = Category.builder()
                .seq(1L)
                .name(requestedCategoryName)
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
                .category(testCategory1)
                .brand(testBrand)
                .price(1000)
                .build();

        List<Product> products = List.of(product1, product2);
        given(categoryRepository.existsByName(requestedCategoryName)).willReturn(true);
        given(productRepository.getLowestProductsByCategory(any())).willReturn(products);

        List<ProductDto> productDtos = products.stream().map(ProductDto::from).toList();

        //when
        List<ProductDto> result = productService.getLowestProductsByCategory(requestedCategoryName);

        //then
        assertThat(result).hasSize(productDtos.size());
        assertThat(result).hasSameElementsAs(productDtos);
    }

    @Test
    @DisplayName("존재하지 않는 카테고리 명이 요청되면 예외가 발생한다.")
    void getLowestProductsByNotExistCategoryNameException() {
        //given
        given(categoryRepository.existsByName(any())).willReturn(false);

        //when & then
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> productService.getLowestProductsByCategory(any()))
                .withMessage("해당 카테고리를 찾을 수 없습니다.");

    }

    @Test
    @DisplayName("요청받은 카테고리명에 해당하는 최고가 상품들이 조회되어야 한다.")
    void getHighestProductsByCategoryName() {
        //given
        String requestedCategoryName = "카테고리1";
        Category testCategory1 = Category.builder()
                .seq(1L)
                .name(requestedCategoryName)
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
                .category(testCategory1)
                .brand(testBrand)
                .price(1000)
                .build();

        List<Product> products = List.of(product1, product2);
        given(categoryRepository.existsByName(requestedCategoryName)).willReturn(true);
        given(productRepository.getHighestProductsByCategory(any())).willReturn(products);

        List<ProductDto> productDtos = products.stream().map(ProductDto::from).toList();

        //when
        List<ProductDto> result = productService.getHighestProductsByCategory(requestedCategoryName);

        //then
        assertThat(result).hasSize(productDtos.size());
        assertThat(result).hasSameElementsAs(productDtos);
    }

    @Test
    @DisplayName("존재하지 않는 카테고리 명이 요청되면 예외가 발생한다.")
    void getHighestProductsByNotExistCategoryNameException() {
        //given
        given(categoryRepository.existsByName(any())).willReturn(false);

        //when & then
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> productService.getHighestProductsByCategory(any()))
                .withMessage("해당 카테고리를 찾을 수 없습니다.");

    }
}