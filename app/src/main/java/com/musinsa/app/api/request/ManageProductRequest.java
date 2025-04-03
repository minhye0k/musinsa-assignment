package com.musinsa.app.api.request;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.musinsa.common.exception.CustomException;
import com.musinsa.product.brand.dto.BrandDto;
import com.musinsa.product.dto.ProductDto;

public record ManageProductRequest(Action action,
                                   Target target,
                                   Product product,
                                   Brand brand) {

    public void validate() {
        if (action == null) throw CustomException.badRequest("실행 할 기능을 입력하지 않았습니다.");
        if (target == null) throw CustomException.badRequest("기능이 수행될 대상을 입력하지 않았습니다.");

        if (target == Target.PRODUCT) {
            if (product == null) throw CustomException.badRequest("상품 정보를 입력하지 않았습니다.");
            product.validate(action);
        } else if (target == Target.BRAND) {
            if (brand == null) throw CustomException.badRequest("브랜드 정보를 입력하지 않았습니다.");
            brand.validate(action);
        } else {
            throw CustomException.badRequest("지원하지 않는 대상입니다.");
        }
    }

    public enum Target {
        PRODUCT, BRAND;

        @JsonCreator
        public static Target fromString(String value) {
            if(value == null) return null;

            for (Target target : Target.values()) {
                if(target.name().equalsIgnoreCase(value)) return target;
            }

            throw CustomException.badRequest("지원하지 않는 대상입니다.");
        }
    }

    public enum Action {
        CREATE, UPDATE, DELETE;

        @JsonCreator
        public static Action fromString(String value) {
            if(value == null) return null;
            for (Action action : Action.values()) {
                if(action.name().equalsIgnoreCase(value)) return action;
            }

            throw CustomException.badRequest("지원하지 않는 기능입니다.");
        }
    }

    public record Brand(Long seq,
                        String name) {

        public void validate(Action action) {
            if (action == Action.DELETE ) {
                if (this.seq == null) throw CustomException.badRequest("삭제 할 브랜드의 번호를 입력하지 않았습니다.");
            } else if (action == Action.CREATE|| action == Action.UPDATE) {
                if(action == Action.UPDATE){
                    if (this.seq == null) throw CustomException.badRequest("수정 할 브랜드의 번호를 입력하지 않았습니다.");
                }
                if (this.name == null) throw CustomException.badRequest("브랜드 명을 입력하지 않았습니다.");
            }
        }

        public BrandDto toBrandDto() {
            return BrandDto.builder()
                    .name(this.name)
                    .build();
        }
    }

    public record Product(Long seq,
                          String brand,
                          String category,
                          Long price) {
        public void validate(Action action) {
            if (action == Action.DELETE) {
                if (this.seq == null) throw CustomException.badRequest("삭제 할 상품을 입력하지 않았습니다.");
            } else if (action == Action.CREATE || action == Action.UPDATE) {
                if(action == Action.UPDATE){
                    if (this.seq == null) throw CustomException.badRequest("수정 할 상품을 입력하지 않았습니다.");
                }
                if (this.brand == null) throw CustomException.badRequest("상품 브랜드를 입력하지 않았습니다.");
                if (this.category == null) throw CustomException.badRequest("상품 카테고리를 입력하지 않았습니다.");
                if (this.price == null) throw CustomException.badRequest("상품 가격을 입력하지 않았습니다.");
                if (this.price < 0) throw CustomException.badRequest("가격은 0 보다 커야합니다.");
            }
        }

        public ProductDto toProductDto() {
            return ProductDto.builder()
                    .brand(this.brand)
                    .category(this.category)
                    .price(this.price)
                    .build();
        }
    }
}
