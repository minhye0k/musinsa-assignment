package com.musinsa.core.domain.product.dao;

import com.musinsa.core.domain.product.dto.ProductQueryParam;
import com.musinsa.core.domain.product.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepositoryCustom {
    List<Product> getLowestProductsByCategory(ProductQueryParam productQueryParam);

    List<Product> getHighestProductsByCategory(ProductQueryParam productQueryParam);

    List<Product> getLowestProductsByBrandAndCategory();
}
