package com.musinsa.common.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CacheType {
    LOWEST_PRODUCTS_BY_CATEGORY("lowestProductsByCategory", 5*60, 1),
    LOWEST_PRODUCTS_BY_BRAND("lowestProductsByBrand", 5*60, 1),
    // 정책상 최대 카테고리는 20개라고 가정하여 max size 20으로 설정
    LOWEST_PRODUCT_BY_CATEGORY("lowestProduct", 5*60, 20),
    HIGHEST_PRODUCT_BY_CATEGORY("highestProduct", 5*60, 20),
    ;

    private final String cacheName;
    private final int expireSecondAfterWrite;
    private final int maximumSize;
}
