package com.musinsa.app.api.facade;

import com.musinsa.app.api.request.ManageProductRequest;
import com.musinsa.app.api.response.ManageProductResponse;
import com.musinsa.common.exception.CustomException;
import com.musinsa.product.brand.BrandService;
import com.musinsa.product.brand.dto.BrandDto;
import com.musinsa.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminFacade {
    private final ProductService productService;
    private final BrandService brandService;

    public ManageProductResponse manageProduct(ManageProductRequest manageProductRequest) {
        ManageProductRequest.Action action = manageProductRequest.action();
        ManageProductRequest.Target target = manageProductRequest.target();
        ManageProductRequest.Product product = manageProductRequest.product();
        ManageProductRequest.Brand brand = manageProductRequest.brand();

        String message;
        switch (action) {
            case CREATE: {
                if (target == ManageProductRequest.Target.PRODUCT) {
                    Long productSeq = productService.create(product.toProductDto());
                    message = String.format("%s번 상품이 생성되었습니다.", productSeq);
                } else if (target == ManageProductRequest.Target.BRAND) {
                    BrandDto brandDto = brand.toBrandDto();
                    Long brandSeq = brandService.create(brandDto);
                    message = String.format("%s (고유번호 : %s) 브랜드가 생성되었습니다.", brandDto.name(), brandSeq);
                } else {
                    throw CustomException.badRequest("지원하지 않는 관리 대상입니다.");
                }
                break;
            }
            case UPDATE: {
                if (target == ManageProductRequest.Target.PRODUCT) {
                    Long productSeq = productService.update(product.seq(), product.toProductDto());
                    message = String.format("%s번 상품이 수정되었습니다.", productSeq);
                } else if (target == ManageProductRequest.Target.BRAND) {
                    String brandName = brandService.update(brand.seq(), brand.toBrandDto());
                    message = String.format("%s 브랜드 정보가 수정되었습니다.", brandName);
                } else {
                    throw CustomException.badRequest("지원하지 않는 관리 대상입니다.");
                }
                break;
            }
            case DELETE: {
                if (target == ManageProductRequest.Target.PRODUCT) {
                    Long productSeq = productService.delete(product.seq());
                    message = String.format("%s번 상품이 삭제되었습니다.", productSeq);
                } else if (target == ManageProductRequest.Target.BRAND) {
                    String brandName = brandService.delete(brand.seq());
                    message = String.format("%s 브랜드가 삭제되었습니다.", brandName);
                } else {
                    throw CustomException.badRequest("지원하지 않는 관리 대상입니다.");
                }
                break;
            }
            default:
                throw CustomException.badRequest("지원하지 않는 기능입니다.");
        }

        return ManageProductResponse.builder()
                .message(message)
                .build();
    }
}
