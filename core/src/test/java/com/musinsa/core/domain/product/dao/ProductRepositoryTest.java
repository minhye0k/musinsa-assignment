package com.musinsa.core.domain.product.dao;

import com.musinsa.core.RepositoryTest;
import com.musinsa.core.domain.brand.entity.Brand;
import com.musinsa.core.domain.category.entity.Category;
import com.musinsa.core.domain.product.dto.ProductQueryParam;
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
        List<Product> lowestProductsByCategory = productRepository.getLowestProductsByCategory(ProductQueryParam.empty());

        //then
        assertThat(lowestProductsByCategory).hasSize(lowestProducts.size());
        assertThat(lowestProductsByCategory).hasSameElementsAs(lowestProducts);
    }

    @Test
    @DisplayName("요청 받은 카테고리 이름에 해당하는 최저가 상품들이 조회되어야 한다.")
    public void getLowestProductsBySpecificCategory(){
        //given
        String requestedCategoryName = "카테고리1";
        Category testCategory1 = Category.builder()
                .name(requestedCategoryName)
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
        Product lowestProduct2 = Product.builder()
                .category(testCategory1)
                .brand(testBrand)
                .price(500)
                .build();
        Product product2 = Product.builder()
                .category(testCategory2)
                .brand(testBrand)
                .price(2000)
                .build();

        productRepository.saveAll(List.of(product1, product2,lowestProduct1,lowestProduct2));
        List<Product> lowestProducts = List.of(lowestProduct1, lowestProduct2);

        //when
        ProductQueryParam productQueryParam = ProductQueryParam.builder()
                .categoryName(requestedCategoryName)
                .build();
        List<Product> lowestProductsByCategory = productRepository.getLowestProductsByCategory(productQueryParam);

        //then
        assertThat(lowestProductsByCategory).hasSize(lowestProducts.size());
        assertThat(lowestProductsByCategory).hasSameElementsAs(lowestProducts);
    }

    @Test
    @DisplayName("카테고리별 최고가 상품들이 조회되어야 한다.")
    public void getHighestProductsByCategory(){
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
        Product highestProduct1 = Product.builder()
                .category(testCategory1)
                .brand(testBrand)
                .price(5000)
                .build();
        Product product2 = Product.builder()
                .category(testCategory2)
                .brand(testBrand)
                .price(2000)
                .build();
        Product highestProduct2 = Product.builder()
                .category(testCategory2)
                .brand(testBrand)
                .price(3000)
                .build();

        productRepository.saveAll(List.of(product1, product2,highestProduct1,highestProduct2));
        List<Product> highestProducts = List.of(highestProduct1, highestProduct2);

        //when
        List<Product> lowestProductsByCategory = productRepository.getHighestProductsByCategory(ProductQueryParam.empty());

        //then
        assertThat(lowestProductsByCategory).hasSize(highestProducts.size());
        assertThat(lowestProductsByCategory).hasSameElementsAs(highestProducts);
    }

    @Test
    @DisplayName("요청 받은 카테고리 이름에 해당하는 최고가 상품들이 조회되어야 한다.")
    public void getHighestProductsBySpecificCategory(){
        //given
        String requestedCategoryName = "카테고리1";
        Category testCategory1 = Category.builder()
                .name(requestedCategoryName)
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
        Product highestProduct1 = Product.builder()
                .category(testCategory1)
                .brand(testBrand)
                .price(5000)
                .build();
        Product highestProduct2 = Product.builder()
                .category(testCategory1)
                .brand(testBrand)
                .price(5000)
                .build();
        Product product2 = Product.builder()
                .category(testCategory2)
                .brand(testBrand)
                .price(2000)
                .build();

        productRepository.saveAll(List.of(product1, product2,highestProduct1,highestProduct2));
        List<Product> highestProducts = List.of(highestProduct1, highestProduct2);

        //when
        ProductQueryParam productQueryParam = ProductQueryParam.builder()
                .categoryName(requestedCategoryName)
                .build();
        List<Product> lowestProductsByCategory = productRepository.getHighestProductsByCategory(productQueryParam);

        //then
        assertThat(lowestProductsByCategory).hasSize(highestProducts.size());
        assertThat(lowestProductsByCategory).hasSameElementsAs(highestProducts);
    }

    @Test
    @DisplayName("모든 브랜드 및 카테고리 별 최저가 상품이 조회되어야 한다.")
    public void getLowestProductsByBrandAndCategory(){
        //given
        Category testCategory1 = Category.builder()
                .name("카테고리1")
                .build();
        Category testCategory2 = Category.builder()
                .name("카테고리2")
                .build();

        Brand testBrand1 = Brand.builder()
                .name("브랜드1")
                .build();

        Brand testBrand2 = Brand.builder()
                .name("브랜드2")
                .build();

        Product product1 = Product.builder()
                .category(testCategory1)
                .brand(testBrand1)
                .price(4000)
                .build();
        Product lowestProduct1 = Product.builder()
                .category(testCategory1)
                .brand(testBrand1)
                .price(3000)
                .build();
        Product product2 = Product.builder()
                .category(testCategory2)
                .brand(testBrand2)
                .price(2000)
                .build();
        Product lowestProduct2 = Product.builder()
                .category(testCategory2)
                .brand(testBrand2)
                .price(1000)
                .build();
        Product lowestProduct3 = Product.builder()
                .category(testCategory1)
                .brand(testBrand2)
                .price(1000)
                .build();

        productRepository.saveAll(List.of(product1, product2,lowestProduct1,lowestProduct2, lowestProduct3));
        List<Product> lowestBrandProducts = List.of(lowestProduct1, lowestProduct2, lowestProduct3);

        //when
        List<Product> result = productRepository.getLowestProductsByBrandAndCategory();

        //then
        assertThat(result).hasSize(lowestBrandProducts.size());
        assertThat(result).hasSameElementsAs(lowestBrandProducts);
    }
}