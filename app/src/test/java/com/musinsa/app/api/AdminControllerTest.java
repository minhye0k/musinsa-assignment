package com.musinsa.app.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.musinsa.app.AppTest;
import com.musinsa.app.api.request.ManageProductRequest;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AppTest
class AdminControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    ProductService productService;
    @Autowired
    BrandService brandService;
    @Autowired
    CategoryService categoryService;

    @Test
    @DisplayName("기능 입력하지 않으면 예외 발생")
    void actionNotExistException() throws Exception {
        //given
        ManageProductRequest manageProductRequest = ManageProductRequest.builder()
                .target(ManageProductRequest.Target.PRODUCT)
                .build();

        String requestBody = objectMapper.writeValueAsString(manageProductRequest);

        //when
        ResultActions actions = mockMvc.perform(post("/api/v1/admin")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        );

        //then
        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("실행 할 기능을 입력하지 않았습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("관리 대상 입력하지 않으면 예외 발생")
    void targetMissedException() throws Exception {
        //given
        ManageProductRequest manageProductRequest = ManageProductRequest.builder()
                .action(ManageProductRequest.Action.CREATE)
                .build();

        String requestBody = objectMapper.writeValueAsString(manageProductRequest);

        //when
        ResultActions actions = mockMvc.perform(post("/api/v1/admin")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        );

        //then
        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("기능이 수행될 대상을 입력하지 않았습니다."))
                .andDo(print());

    }

    @Test
    @DisplayName("상품 관리 선택 후 정보 없으면 예외 발생")
    void productMissedException() throws Exception {
        //given
        ManageProductRequest manageProductRequest = ManageProductRequest.builder()
                .action(ManageProductRequest.Action.CREATE)
                .target(ManageProductRequest.Target.PRODUCT)
                .build();

        String requestBody = objectMapper.writeValueAsString(manageProductRequest);

        //when
        ResultActions actions = mockMvc.perform(post("/api/v1/admin")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        );

        //then
        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("상품 정보를 입력하지 않았습니다."))
                .andDo(print());

    }

    @Test
    @DisplayName("브랜드 관리 선택 후 정보 없으면 예외 발생")
    void brandNotExistException() throws Exception {
        //given
        ManageProductRequest manageProductRequest = ManageProductRequest.builder()
                .action(ManageProductRequest.Action.CREATE)
                .target(ManageProductRequest.Target.BRAND)
                .build();

        String requestBody = objectMapper.writeValueAsString(manageProductRequest);

        //when
        ResultActions actions = mockMvc.perform(post("/api/v1/admin")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        );

        //then
        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("브랜드 정보를 입력하지 않았습니다."))
                .andDo(print());

    }

    @Test
    @DisplayName("[상품 생성]")
    void createProduct() throws Exception {
        //given
        CategoryDto categoryDto = CategoryDto.builder()
                .name("카테고리")
                .build();
        categoryService.create(categoryDto);
        BrandDto brandDto = BrandDto.builder()
                .name("브랜드1")
                .build();
        brandService.create(brandDto);

        ManageProductRequest manageProductRequest = ManageProductRequest.builder()
                .action(ManageProductRequest.Action.CREATE)
                .target(ManageProductRequest.Target.PRODUCT)
                .product(ManageProductRequest.Product.builder()
                        .category(categoryDto.name())
                        .brand(brandDto.name())
                        .price(2000L)
                        .build())
                .build();

        String requestBody = objectMapper.writeValueAsString(manageProductRequest);

        //when
        ResultActions actions = mockMvc.perform(post("/api/v1/admin")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        );

        //then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("[상품 생성] 브랜드 존재하지 않으면 예외 발생")
    void createProductBrandNotFoundException() throws Exception {
        //given
        CategoryDto categoryDto = CategoryDto.builder()
                .name("카테고리")
                .build();
        categoryService.create(categoryDto);

        ManageProductRequest manageProductRequest = ManageProductRequest.builder()
                .action(ManageProductRequest.Action.CREATE)
                .target(ManageProductRequest.Target.PRODUCT)
                .product(ManageProductRequest.Product.builder()
                        .category(categoryDto.name())
                        .brand("브랜드")
                        .price(2000L)
                        .build())
                .build();

        String requestBody = objectMapper.writeValueAsString(manageProductRequest);

        //when
        ResultActions actions = mockMvc.perform(post("/api/v1/admin")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        );

        //then
        actions.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("지정하신 브랜드를 찾을 수 없습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("[상품 생성] 브랜드 입력하지 않으면 예외 발생")
    void createProductBrandNotExistException() throws Exception {
        //given
        ManageProductRequest manageProductRequest = ManageProductRequest.builder()
                .action(ManageProductRequest.Action.CREATE)
                .target(ManageProductRequest.Target.PRODUCT)
                .product(ManageProductRequest.Product.builder()
                        .category("카테고리")
                        .price(2000L)
                        .build())
                .build();

        String requestBody = objectMapper.writeValueAsString(manageProductRequest);

        //when
        ResultActions actions = mockMvc.perform(post("/api/v1/admin")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        );

        //then
        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("상품 브랜드를 입력하지 않았습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("[상품 생성] 카테고리 존재하지 않으면 예외 발생")
    void createProductCategoryNotFoundException() throws Exception {
        //given
        BrandDto brandDto = BrandDto.builder()
                .name("브랜드")
                .build();
        brandService.create(brandDto);

        ManageProductRequest manageProductRequest = ManageProductRequest.builder()
                .action(ManageProductRequest.Action.CREATE)
                .target(ManageProductRequest.Target.PRODUCT)
                .product(ManageProductRequest.Product.builder()
                        .category("카테고리")
                        .brand(brandDto.name())
                        .price(2000L)
                        .build())
                .build();

        String requestBody = objectMapper.writeValueAsString(manageProductRequest);

        //when
        ResultActions actions = mockMvc.perform(post("/api/v1/admin")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        );

        //then
        actions.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("지정하신 카테고리를 찾을 수 없습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("[상품 생성] 카테고리 입력하지 않으면 예외 발생")
    void createProductCategoryNotExistException() throws Exception {
        //given
        ManageProductRequest manageProductRequest = ManageProductRequest.builder()
                .action(ManageProductRequest.Action.CREATE)
                .target(ManageProductRequest.Target.PRODUCT)
                .product(ManageProductRequest.Product.builder()
                        .brand("브랜드")
                        .price(2000L)
                        .build())
                .build();

        String requestBody = objectMapper.writeValueAsString(manageProductRequest);

        //when
        ResultActions actions = mockMvc.perform(post("/api/v1/admin")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        );

        //then
        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("상품 카테고리를 입력하지 않았습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("[상품 생성] 가격 입력하지 않으면 예외 발생")
    void createProductPriceNotExistException() throws Exception {
        //given
        ManageProductRequest manageProductRequest = ManageProductRequest.builder()
                .action(ManageProductRequest.Action.CREATE)
                .target(ManageProductRequest.Target.PRODUCT)
                .product(ManageProductRequest.Product.builder()
                        .seq(1L)
                        .brand("브랜드")
                        .category("카테고리1")
                        .build())
                .build();

        String requestBody = objectMapper.writeValueAsString(manageProductRequest);

        //when
        ResultActions actions = mockMvc.perform(post("/api/v1/admin")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        );

        //then
        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("상품 가격을 입력하지 않았습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("[상품 생성] 가격 0보다 작으면 예외 발생")
    void createProductPriceLessThanZero() throws Exception {
        //given
        ManageProductRequest manageProductRequest = ManageProductRequest.builder()
                .action(ManageProductRequest.Action.CREATE)
                .target(ManageProductRequest.Target.PRODUCT)
                .product(ManageProductRequest.Product.builder()
                        .seq(1L)
                        .brand("브랜드")
                        .category("카테고리1")
                        .price(-1L)
                        .build())
                .build();

        String requestBody = objectMapper.writeValueAsString(manageProductRequest);

        //when
        ResultActions actions = mockMvc.perform(post("/api/v1/admin")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        );

        //then
        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("가격은 0 보다 커야합니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("[상품 수정]")
    void updateProduct() throws Exception {
        //given
        CategoryDto categoryDto = CategoryDto.builder()
                .name("카테고리")
                .build();
        categoryService.create(categoryDto);
        BrandDto brandDto = BrandDto.builder()
                .name("브랜드1")
                .build();
        brandService.create(brandDto);

        ProductDto productDto = ProductDto.builder()
                .brand(brandDto.name())
                .category(categoryDto.name())
                .price(2000L)
                .build();
        Long productSeq = productService.create(productDto);

        ManageProductRequest manageProductRequest = ManageProductRequest.builder()
                .action(ManageProductRequest.Action.UPDATE)
                .target(ManageProductRequest.Target.PRODUCT)
                .product(ManageProductRequest.Product.builder()
                        .seq(productSeq)
                        .category(categoryDto.name())
                        .brand(brandDto.name())
                        .price(2000L)
                        .build())
                .build();

        String requestBody = objectMapper.writeValueAsString(manageProductRequest);

        //when
        ResultActions actions = mockMvc.perform(post("/api/v1/admin")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        );

        //then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("[상품 수정] 상품 존재하지 않으면 예외 발생")
    void updateProductNotFoundException() throws Exception {
        //given
        CategoryDto categoryDto = CategoryDto.builder()
                .name("카테고리")
                .build();
        categoryService.create(categoryDto);
        BrandDto brandDto = BrandDto.builder()
                .name("브랜드1")
                .build();
        brandService.create(brandDto);

        Long productSeq = 1L;

        ManageProductRequest manageProductRequest = ManageProductRequest.builder()
                .action(ManageProductRequest.Action.UPDATE)
                .target(ManageProductRequest.Target.PRODUCT)
                .product(ManageProductRequest.Product.builder()
                        .seq(productSeq)
                        .category(categoryDto.name())
                        .brand("브랜드2")
                        .price(2000L)
                        .build())
                .build();

        String requestBody = objectMapper.writeValueAsString(manageProductRequest);

        //when
        ResultActions actions = mockMvc.perform(post("/api/v1/admin")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        );

        //then
        actions.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("해당 상품을 찾을 수 없습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("[상품 수정] 상품 입력하지 않으면 예외 발생")
    void updateProductNotExistException() throws Exception {
        //given

        ManageProductRequest manageProductRequest = ManageProductRequest.builder()
                .action(ManageProductRequest.Action.UPDATE)
                .target(ManageProductRequest.Target.PRODUCT)
                .product(ManageProductRequest.Product.builder()
                        .category("카테고리")
                        .brand("브랜드")
                        .price(2000L)
                        .build())
                .build();

        String requestBody = objectMapper.writeValueAsString(manageProductRequest);

        //when
        ResultActions actions = mockMvc.perform(post("/api/v1/admin")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        );

        //then
        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("수정 할 상품을 입력하지 않았습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("[상품 수정] 브랜드 존재하지 않으면 예외 발생")
    void updateProductBrandNotFoundException() throws Exception {
        //given
        CategoryDto categoryDto = CategoryDto.builder()
                .name("카테고리")
                .build();
        categoryService.create(categoryDto);
        BrandDto brandDto = BrandDto.builder()
                .name("브랜드1")
                .build();
        brandService.create(brandDto);

        ProductDto productDto = ProductDto.builder()
                .brand(brandDto.name())
                .category(categoryDto.name())
                .price(2000L)
                .build();
        Long productSeq = productService.create(productDto);

        ManageProductRequest manageProductRequest = ManageProductRequest.builder()
                .action(ManageProductRequest.Action.UPDATE)
                .target(ManageProductRequest.Target.PRODUCT)
                .product(ManageProductRequest.Product.builder()
                        .seq(productSeq)
                        .category(categoryDto.name())
                        .brand("브랜드2")
                        .price(2000L)
                        .build())
                .build();

        String requestBody = objectMapper.writeValueAsString(manageProductRequest);

        //when
        ResultActions actions = mockMvc.perform(post("/api/v1/admin")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        );

        //then
        actions.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("지정하신 브랜드를 찾을 수 없습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("[상품 수정] 가격 입력하지 않으면 예외 발생")
    void updateProductPriceNotExistException() throws Exception {
        //given
        ManageProductRequest manageProductRequest = ManageProductRequest.builder()
                .action(ManageProductRequest.Action.UPDATE)
                .target(ManageProductRequest.Target.PRODUCT)
                .product(ManageProductRequest.Product.builder()
                        .seq(1L)
                        .brand("브랜드")
                        .category("카테고리1")
                        .build())
                .build();

        String requestBody = objectMapper.writeValueAsString(manageProductRequest);

        //when
        ResultActions actions = mockMvc.perform(post("/api/v1/admin")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        );

        //then
        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("상품 가격을 입력하지 않았습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("[상품 수정] 가격 0보다 작으면 예외 발생")
    void updateProductPriceLessThanZero() throws Exception {
        //given
        ManageProductRequest manageProductRequest = ManageProductRequest.builder()
                .action(ManageProductRequest.Action.UPDATE)
                .target(ManageProductRequest.Target.PRODUCT)
                .product(ManageProductRequest.Product.builder()
                        .seq(1L)
                        .brand("브랜드")
                        .category("카테고리1")
                        .price(-1L)
                        .build())
                .build();

        String requestBody = objectMapper.writeValueAsString(manageProductRequest);

        //when
        ResultActions actions = mockMvc.perform(post("/api/v1/admin")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        );

        //then
        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("가격은 0 보다 커야합니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("[상품 수정] 브랜드 입력하지 않으면 예외 발생")
    void updateProductBrandNotExistException() throws Exception {
        //given
        ManageProductRequest manageProductRequest = ManageProductRequest.builder()
                .action(ManageProductRequest.Action.UPDATE)
                .target(ManageProductRequest.Target.PRODUCT)
                .product(ManageProductRequest.Product.builder()
                        .seq(1L)
                        .category("카테고리1")
                        .price(2000L)
                        .build())
                .build();

        String requestBody = objectMapper.writeValueAsString(manageProductRequest);

        //when
        ResultActions actions = mockMvc.perform(post("/api/v1/admin")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        );

        //then
        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("상품 브랜드를 입력하지 않았습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("[상품 수정] 카테고리 존재하지 않으면 예외 발생")
    void updateProductCategoryNotFoundException() throws Exception {
        //given
        CategoryDto categoryDto = CategoryDto.builder()
                .name("카테고리")
                .build();
        categoryService.create(categoryDto);
        BrandDto brandDto = BrandDto.builder()
                .name("브랜드1")
                .build();
        brandService.create(brandDto);

        ProductDto productDto = ProductDto.builder()
                .brand(brandDto.name())
                .category(categoryDto.name())
                .price(2000L)
                .build();
        Long productSeq = productService.create(productDto);

        ManageProductRequest manageProductRequest = ManageProductRequest.builder()
                .action(ManageProductRequest.Action.UPDATE)
                .target(ManageProductRequest.Target.PRODUCT)
                .product(ManageProductRequest.Product.builder()
                        .seq(productSeq)
                        .category("카테고리1")
                        .brand(brandDto.name())
                        .price(2000L)
                        .build())
                .build();

        String requestBody = objectMapper.writeValueAsString(manageProductRequest);

        //when
        ResultActions actions = mockMvc.perform(post("/api/v1/admin")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        );

        //then
        actions.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("지정하신 카테고리를 찾을 수 없습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("[상품 수정] 카테고리 입력하지 않으면 예외 발생")
    void updateProductCategoryNotExistException() throws Exception {
        //given
        ManageProductRequest manageProductRequest = ManageProductRequest.builder()
                .action(ManageProductRequest.Action.UPDATE)
                .target(ManageProductRequest.Target.PRODUCT)
                .product(ManageProductRequest.Product.builder()
                        .seq(1L)
                        .brand("브랜드")
                        .price(2000L)
                        .build())
                .build();

        String requestBody = objectMapper.writeValueAsString(manageProductRequest);

        //when
        ResultActions actions = mockMvc.perform(post("/api/v1/admin")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        );

        //then
        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("상품 카테고리를 입력하지 않았습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("[상품 삭제]")
    void deleteProduct() throws Exception {
        //given
        CategoryDto categoryDto = CategoryDto.builder()
                .name("카테고리")
                .build();
        categoryService.create(categoryDto);
        BrandDto brandDto = BrandDto.builder()
                .name("브랜드1")
                .build();
        brandService.create(brandDto);

        ProductDto productDto = ProductDto.builder()
                .brand(brandDto.name())
                .category(categoryDto.name())
                .price(2000L)
                .build();
        Long productSeq = productService.create(productDto);

        ManageProductRequest manageProductRequest = ManageProductRequest.builder()
                .action(ManageProductRequest.Action.DELETE)
                .target(ManageProductRequest.Target.PRODUCT)
                .product(ManageProductRequest.Product.builder()
                        .seq(productSeq)
                        .category(categoryDto.name())
                        .brand(brandDto.name())
                        .price(2000L)
                        .build())
                .build();

        String requestBody = objectMapper.writeValueAsString(manageProductRequest);

        //when
        ResultActions actions = mockMvc.perform(post("/api/v1/admin")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        );

        //then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("[상품 삭제] 삭제 대상 입력하지 않으면 예외 발생")
    void deleteProductNotExistException() throws Exception {
        //given
        ManageProductRequest manageProductRequest = ManageProductRequest.builder()
                .action(ManageProductRequest.Action.DELETE)
                .target(ManageProductRequest.Target.PRODUCT)
                .product(ManageProductRequest.Product.builder()
                        .brand("브랜드")
                        .price(2000L)
                        .build())
                .build();

        String requestBody = objectMapper.writeValueAsString(manageProductRequest);

        //when
        ResultActions actions = mockMvc.perform(post("/api/v1/admin")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        );

        //then
        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("삭제 할 상품을 입력하지 않았습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("[상품 삭제] 삭제 대상 없으면 예외 발생")
    void deleteProductNotFoundException() throws Exception {
        //given
        Long productSeq = 1L;
        ManageProductRequest manageProductRequest = ManageProductRequest.builder()
                .action(ManageProductRequest.Action.DELETE)
                .target(ManageProductRequest.Target.PRODUCT)
                .product(ManageProductRequest.Product.builder()
                        .seq(productSeq)
                        .build())
                .build();

        String requestBody = objectMapper.writeValueAsString(manageProductRequest);

        //when
        ResultActions actions = mockMvc.perform(post("/api/v1/admin")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        );

        //then
        actions.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("해당 상품을 찾을 수 없습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("[브랜드 생성]")
    void createBrand() throws Exception {
        //given
        ManageProductRequest manageProductRequest = ManageProductRequest.builder()
                .action(ManageProductRequest.Action.CREATE)
                .target(ManageProductRequest.Target.BRAND)
                .brand(ManageProductRequest.Brand.builder()
                        .name("브랜드")
                        .build())
                .build();

        String requestBody = objectMapper.writeValueAsString(manageProductRequest);

        //when
        ResultActions actions = mockMvc.perform(post("/api/v1/admin")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        );

        //then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("[브랜드 생성] 브랜드명 중복되면 예외 발생")
    void createBrandNameDuplicationException() throws Exception {
        //given
        BrandDto brandDto = BrandDto.builder()
                .name("브랜드")
                .build();
        brandService.create(brandDto);

        ManageProductRequest manageProductRequest = ManageProductRequest.builder()
                .action(ManageProductRequest.Action.CREATE)
                .target(ManageProductRequest.Target.BRAND)
                .brand(ManageProductRequest.Brand.builder()
                        .name(brandDto.name())
                        .build())
                .build();

        String requestBody = objectMapper.writeValueAsString(manageProductRequest);

        //when
        ResultActions actions = mockMvc.perform(post("/api/v1/admin")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        );

        //then
        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("이미 존재하는 브랜드 명입니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("[브랜드 생성] 브랜드명 정보 없으면 예외 발생")
    void createBrandNameNotExistException() throws Exception {
        //given
        ManageProductRequest manageProductRequest = ManageProductRequest.builder()
                .action(ManageProductRequest.Action.CREATE)
                .target(ManageProductRequest.Target.BRAND)
                .brand(ManageProductRequest.Brand.builder()
                        .build())
                .build();

        String requestBody = objectMapper.writeValueAsString(manageProductRequest);

        //when
        ResultActions actions = mockMvc.perform(post("/api/v1/admin")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        );

        //then
        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("브랜드 명을 입력하지 않았습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("[브랜드 수정]")
    void updateBrand() throws Exception {
        //given
        BrandDto brandDto = BrandDto.builder()
                .name("브랜드")
                .build();
        Long brandSeq = brandService.create(brandDto);

        ManageProductRequest manageProductRequest = ManageProductRequest.builder()
                .action(ManageProductRequest.Action.UPDATE)
                .target(ManageProductRequest.Target.BRAND)
                .brand(ManageProductRequest.Brand.builder()
                        .seq(brandSeq)
                        .name("브랜드1")
                        .build())
                .build();

        String requestBody = objectMapper.writeValueAsString(manageProductRequest);

        //when
        ResultActions actions = mockMvc.perform(post("/api/v1/admin")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        );

        //then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("[브랜드 수정] 수정할 브랜드 입력하지 않으면 예외 발생")
    void updateBrandNotExistException() throws Exception {
        //given
        BrandDto brandDto = BrandDto.builder()
                .name("브랜드")
                .build();
        Long brandSeq = brandService.create(brandDto);

        ManageProductRequest manageProductRequest = ManageProductRequest.builder()
                .action(ManageProductRequest.Action.UPDATE)
                .target(ManageProductRequest.Target.BRAND)
                .brand(ManageProductRequest.Brand.builder()
                        .name("브랜드1")
                        .build())
                .build();

        String requestBody = objectMapper.writeValueAsString(manageProductRequest);

        //when
        ResultActions actions = mockMvc.perform(post("/api/v1/admin")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        );

        //then
        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("수정 할 브랜드의 번호를 입력하지 않았습니다."))
                .andDo(print());
    }


    @Test
    @DisplayName("[브랜드 수정] 수정할 브랜드 없으면 예외 발생")
    void updateBrandNotFoundException() throws Exception {
        //given
        Long brandSeq = 1L;

        ManageProductRequest manageProductRequest = ManageProductRequest.builder()
                .action(ManageProductRequest.Action.UPDATE)
                .target(ManageProductRequest.Target.BRAND)
                .brand(ManageProductRequest.Brand.builder()
                        .seq(brandSeq)
                        .name("브랜드1")
                        .build())
                .build();

        String requestBody = objectMapper.writeValueAsString(manageProductRequest);

        //when
        ResultActions actions = mockMvc.perform(post("/api/v1/admin")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        );

        //then
        actions.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("해당 브랜드를 찾을 수 없습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("[브랜드 수정] 수정할 브랜드명 이미 존재하면 예외 발생")
    void updateBrandNameDuplicationException() throws Exception {
        //given
        BrandDto brandDto = BrandDto.builder()
                .name("브랜드")
                .build();
        Long brandSeq = brandService.create(brandDto);


        ManageProductRequest manageProductRequest = ManageProductRequest.builder()
                .action(ManageProductRequest.Action.UPDATE)
                .target(ManageProductRequest.Target.BRAND)
                .brand(ManageProductRequest.Brand.builder()
                        .seq(brandSeq)
                        .name(brandDto.name())
                        .build())
                .build();

        String requestBody = objectMapper.writeValueAsString(manageProductRequest);

        //when
        ResultActions actions = mockMvc.perform(post("/api/v1/admin")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        );

        //then
        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("이미 존재하는 브랜드 명입니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("[브랜드 수정] 수정할 브랜드명 입력하지 않으면 예외 발생")
    void updateBrandNameNotExistException() throws Exception {
        //given
        Long brandSeq = 1L;

        ManageProductRequest manageProductRequest = ManageProductRequest.builder()
                .action(ManageProductRequest.Action.UPDATE)
                .target(ManageProductRequest.Target.BRAND)
                .brand(ManageProductRequest.Brand.builder()
                        .seq(brandSeq)
                        .build())
                .build();

        String requestBody = objectMapper.writeValueAsString(manageProductRequest);

        //when
        ResultActions actions = mockMvc.perform(post("/api/v1/admin")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        );

        //then
        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("브랜드 명을 입력하지 않았습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("[브랜드 삭제]")
    void deleteBrand() throws Exception {
        //given
        BrandDto brandDto = BrandDto.builder()
                .name("브랜드")
                .build();
        Long brandSeq = brandService.create(brandDto);

        ManageProductRequest manageProductRequest = ManageProductRequest.builder()
                .action(ManageProductRequest.Action.DELETE)
                .target(ManageProductRequest.Target.BRAND)
                .brand(ManageProductRequest.Brand.builder()
                        .seq(brandSeq)
                        .build())
                .build();

        String requestBody = objectMapper.writeValueAsString(manageProductRequest);

        //when
        ResultActions actions = mockMvc.perform(post("/api/v1/admin")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        );

        //then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("[브랜드 삭제] 삭제할 브랜드 입력하지 않으면 예외 발생")
    void deleteBrandNotExistException() throws Exception {
        //given
        ManageProductRequest manageProductRequest = ManageProductRequest.builder()
                .action(ManageProductRequest.Action.DELETE)
                .target(ManageProductRequest.Target.BRAND)
                .brand(ManageProductRequest.Brand.builder()
                        .build())
                .build();

        String requestBody = objectMapper.writeValueAsString(manageProductRequest);

        //when
        ResultActions actions = mockMvc.perform(post("/api/v1/admin")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        );

        //then
        actions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("삭제 할 브랜드의 번호를 입력하지 않았습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("[브랜드 삭제] 삭제할 브랜드 없으면 예외 발생")
    void deleteBrandNotFoundException() throws Exception {
        //given
        ManageProductRequest manageProductRequest = ManageProductRequest.builder()
                .action(ManageProductRequest.Action.DELETE)
                .target(ManageProductRequest.Target.BRAND)
                .brand(ManageProductRequest.Brand.builder()
                        .seq(1L)
                        .build())
                .build();

        String requestBody = objectMapper.writeValueAsString(manageProductRequest);

        //when
        ResultActions actions = mockMvc.perform(post("/api/v1/admin")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        );

        //then
        actions.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("해당 브랜드를 찾을 수 없습니다."))
                .andDo(print());
    }
}