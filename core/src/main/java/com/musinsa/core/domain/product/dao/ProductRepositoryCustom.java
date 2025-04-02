package com.musinsa.core.domain.product.dao;

import com.musinsa.core.domain.product.entity.Product;

import java.util.List;

public interface ProductRepositoryCustom {
    List<Product> getLowestProductsByCategory();
}
