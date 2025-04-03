package com.musinsa.app.api.request;


import com.musinsa.product.brand.dto.BrandDto;
import com.musinsa.product.dto.ProductDto;

public record ManageProductRequest(Action action,
                                   Target target,
                                   Product product,
                                   Brand brand) {

    public void validate() {
        if (action == null) throw new IllegalArgumentException("action is null");
        if (target == null) throw new IllegalArgumentException("target is null");

        if (target == Target.PRODUCT) {
            if (product == null) throw new IllegalArgumentException("product is null");
            product.validate(action);
        } else if (target == Target.BRAND) {
            if (brand == null) throw new IllegalArgumentException("brand is null");
            brand.validate(action);
        } else {
            throw new IllegalArgumentException("target is not a valid target");
        }
    }

    public enum Target {
        PRODUCT, BRAND
    }

    public enum Action {
        CREATE, UPDATE, DELETE
    }

    public record Brand(Long seq,
                        String name) {

        public void validate(Action action) {
            if (action == Action.DELETE ) {
                if (this.seq == null) throw new IllegalArgumentException("seq is null");
            } else if (action == Action.CREATE|| action == Action.UPDATE) {
                if(action == Action.UPDATE){
                    if (this.seq == null) throw new IllegalArgumentException("seq is null");
                }
                if (this.name == null) throw new IllegalArgumentException("brand is null");
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
                if (this.seq == null) throw new IllegalArgumentException("seq is null");
            } else if (action == Action.CREATE || action == Action.UPDATE) {
                if(action == Action.UPDATE){
                    if (this.seq == null) throw new IllegalArgumentException("seq is null");
                }
                if (this.brand == null) throw new IllegalArgumentException("brand is null");
                if (this.category == null) throw new IllegalArgumentException("category is null");
                if (this.price == null) throw new IllegalArgumentException("price is null");
                if (this.price < 0) throw new IllegalArgumentException("price must be greater than zero");
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
