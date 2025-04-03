package com.musinsa.product.brand;

import com.musinsa.core.domain.brand.dao.BrandRepository;
import com.musinsa.core.domain.brand.entity.Brand;
import com.musinsa.core.domain.product.entity.Product;
import com.musinsa.product.brand.dto.BrandDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class BrandServiceTest {
    @InjectMocks
    private BrandService brandService;
    @Mock
    private BrandRepository brandRepository;

    @Test
    @DisplayName("브랜드를 등록한다.")
    void create() {
        //given
        long seq = 1L;
        final String name = "브랜드1";
        Brand testBrand = Brand.builder()
                .seq(seq)
                .name(name)
                .build();
        BrandDto brandDto = BrandDto.builder()
                .name(testBrand.getName())
                .build();

        given(brandRepository.existsByName(name)).willReturn(false);
        given(brandRepository.save(any())).willReturn(testBrand);

        //when
        Long result = brandService.create(brandDto);

        //then
        assertThat(result).isEqualTo(seq);
    }

    @Test
    @DisplayName("요청된 브랜드 이름이 이미 존재하면 예외가 발생한다.")
    void createNameDuplicationException() {
        //given
        given(brandRepository.existsByName(any())).willReturn(true);

        //when & then
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> brandService.create(BrandDto.builder().build()))
                .withMessage("이미 존재하는 브랜드 명입니다.");
    }

    @Test
    @DisplayName("브랜드 정보를 업데이트한다.")
    void update() {
        //given
        long seq = 1L;
        final String name = "브랜드1";
        Brand testBrand = Brand.builder()
                .seq(seq)
                .name(name)
                .build();
        BrandDto brandDto = BrandDto.builder()
                .name(testBrand.getName())
                .build();

        given(brandRepository.findById(any())).willReturn(Optional.of(testBrand));
        given(brandRepository.existsByName(name)).willReturn(false);

        //when
        String result = brandService.update(seq, brandDto);

        //then
        assertThat(result).isEqualTo(name);
    }

    @Test
    @DisplayName("요청된 브랜드 이름이 이미 존재하면 예외가 발생한다.")
    void updateNameDuplicationException() {
        //given
        given(brandRepository.findById(any())).willReturn(Optional.of(Brand.builder().build()));
        given(brandRepository.existsByName(any())).willReturn(true);

        //when & then
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> brandService.update(1L, BrandDto.builder().build()))
                .withMessage("이미 존재하는 브랜드 명입니다.");
    }

    @Test
    @DisplayName("브랜드를 삭제한다.")
    void delete() {
        //given
        long seq = 1L;
        final String name = "브랜드1";
        Brand testBrand = Brand.builder()
                .seq(seq)
                .name(name)
                .build();

        given(brandRepository.findBySeqWithProducts(any())).willReturn(Optional.of(testBrand));

        //when
        String result = brandService.delete(seq);

        //then
        assertThat(result).isEqualTo(name);
    }

    @Test
    @DisplayName("삭제하려는 브랜드가 존재하지 않으면 예외가 발생한다.")
    void deleteNotFoundException() {
        //given

        given(brandRepository.findBySeqWithProducts(any())).willReturn(Optional.empty());

        //when & then
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() ->brandService.delete(any()))
                .withMessage("해당 브랜드를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("삭제하려는 브랜드에 상품이 존재하면 예외가 발생한다.")
    void deleteHasProductException() {
        //given
        Product testProduct = Product.builder()
                .seq(1L)
                .price(1000)
                .build();

        Brand testBrand = Brand.builder()
                .name("브랜드1")
                .products(List.of(testProduct))
                .build();

        given(brandRepository.findBySeqWithProducts(any())).willReturn(Optional.of(testBrand));

        //when & then
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> brandService.delete(any()))
                .withMessage("상품이 있는 브랜드는 삭제할 수 없습니다.");
    }
}