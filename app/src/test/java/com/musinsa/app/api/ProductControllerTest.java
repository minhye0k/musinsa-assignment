package com.musinsa.app.api;

import com.musinsa.app.AppTest;
import com.musinsa.product.ProductService;
import com.musinsa.product.brand.BrandService;
import com.musinsa.product.brand.dto.BrandDto;
import com.musinsa.product.category.CategoryService;
import com.musinsa.product.category.dto.CategoryDto;
import com.musinsa.product.dto.ProductDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AppTest
class ProductControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ProductService productService;
    @Autowired
    BrandService brandService;
    @Autowired
    CategoryService categoryService;

    @Test
    @DisplayName("카테고리별 최저가 상품들의 정보를 반환한다.")
    void lowestByCategory() throws Exception {
        //given
        CategoryDto categoryDto = CategoryDto.builder()
                .name("카테고리1")
                .build();
        CategoryDto categoryDto2 = CategoryDto.builder()
                .name("카테고리2")
                .build();
        categoryService.create(categoryDto);
        categoryService.create(categoryDto2);
        BrandDto brandDto = BrandDto.builder()
                .name("브랜드1")
                .build();
        brandService.create(brandDto);

        ProductDto productDto = ProductDto.builder()
                .category(categoryDto.name())
                .brand(brandDto.name())
                .price(3000)
                .build();
        ProductDto lowestProductDto = ProductDto.builder()
                .category(categoryDto.name())
                .brand(brandDto.name())
                .price(1000)
                .build();
        ProductDto productDto3 = ProductDto.builder()
                .category(categoryDto2.name())
                .brand(brandDto.name())
                .price(3000)
                .build();
        ProductDto lowestProductDto2 = ProductDto.builder()
                .category(categoryDto2.name())
                .brand(brandDto.name())
                .price(2000)
                .build();

        productService.create(productDto);
        productService.create(lowestProductDto);
        productService.create(productDto3);
        productService.create(lowestProductDto2);

        //when
        ResultActions actions = mockMvc.perform(get("/api/v1/products/lowest-by-category")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
        );


        //then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.최저가[0].가격").value(1000))
                .andExpect(jsonPath("$.최저가[1].가격").value(2000))
                .andDo(print());

    }

    @Test
    @DisplayName("카테고리별 상품들의 최저가 합을 가진 브랜드를 구하고, 해당 상품들을 반환한다.")
    void lowestByBrand() throws Exception {
        //given
        CategoryDto categoryDto = CategoryDto.builder()
                .name("카테고리1")
                .build();
        CategoryDto categoryDto2 = CategoryDto.builder()
                .name("카테고리2")
                .build();
        categoryService.create(categoryDto);
        categoryService.create(categoryDto2);
        BrandDto brandDto = BrandDto.builder()
                .name("브랜드1")
                .build();
        BrandDto lowestBrandDto = BrandDto.builder()
                .name("브랜드2")
                .build();
        brandService.create(brandDto);
        brandService.create(lowestBrandDto);

        ProductDto productDto = ProductDto.builder()
                .category(categoryDto.name())
                .brand(brandDto.name())
                .price(3000)
                .build();
        ProductDto lowestProductDto = ProductDto.builder()
                .category(categoryDto.name())
                .brand(lowestBrandDto.name())
                .price(1000)
                .build();
        ProductDto productDto2 = ProductDto.builder()
                .category(categoryDto2.name())
                .brand(brandDto.name())
                .price(3000)
                .build();
        ProductDto lowestProductDto2 = ProductDto.builder()
                .category(categoryDto2.name())
                .brand(lowestBrandDto.name())
                .price(2000)
                .build();

        productService.create(productDto);
        productService.create(lowestProductDto);
        productService.create(productDto2);
        productService.create(lowestProductDto2);

        //when
        ResultActions actions = mockMvc.perform(get("/api/v1/products/lowest-by-brand")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
        );


        //then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.최저가.브랜드").value(lowestBrandDto.name()))
                .andExpect(jsonPath("$.최저가.카테고리[0].가격").value(1000))
                .andExpect(jsonPath("$.최저가.카테고리[1].가격").value(2000))
                .andDo(print());
    }

    @Test
    @DisplayName("입력 받은 카테고리의 최저, 최고가 상품 목록을 나열한다.")
    void mostByCategory() throws Exception {
        //given
        CategoryDto categoryDto = CategoryDto.builder()
                .name("카테고리1")
                .build();
        categoryService.create(categoryDto);
        BrandDto brandDto = BrandDto.builder()
                .name("브랜드1")
                .build();
        brandService.create(brandDto);
        ProductDto highestProductDto = ProductDto.builder()
                .category(categoryDto.name())
                .brand(brandDto.name())
                .price(3000)
                .build();
        ProductDto productDto = ProductDto.builder()
                .category(categoryDto.name())
                .brand(brandDto.name())
                .price(2000)
                .build();
        ProductDto lowestProductDto = ProductDto.builder()
                .category(categoryDto.name())
                .brand(brandDto.name())
                .price(1000)
                .build();
        productService.create(productDto);
        productService.create(highestProductDto);
        productService.create(lowestProductDto);

        //when
        ResultActions actions = mockMvc.perform(get("/api/v1/products/most-by-category")
                .param("category", categoryDto.name())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.최저가[0].가격").value(lowestProductDto.price()))
                .andExpect(jsonPath("$.최고가[0].가격").value(highestProductDto.price()))
                .andDo(print());
    }

    @Test
    @DisplayName("입력 받은 카테고리가 존재하지 않으면 예외가 발생한다.")
    void mostByCategoryNotFoundException() throws Exception {
        //given
        String category = "NO-PRESENT-CATEGORY";

        //when
        ResultActions actions = mockMvc.perform(get("/api/v1/products/most-by-category")
                .param("category", category)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        actions.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("해당 카테고리를 찾을 수 없습니다."))
                .andDo(print());
    }
}