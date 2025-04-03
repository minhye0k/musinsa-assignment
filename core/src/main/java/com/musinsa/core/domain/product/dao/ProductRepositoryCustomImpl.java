package com.musinsa.core.domain.product.dao;

import com.musinsa.core.domain.product.dto.ProductQueryParam;
import com.musinsa.core.domain.product.entity.Product;
import com.musinsa.core.domain.product.entity.QProduct;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import static com.musinsa.core.domain.brand.entity.QBrand.brand;
import static com.musinsa.core.domain.category.entity.QCategory.category;
import static com.musinsa.core.domain.product.entity.QProduct.product;

@RequiredArgsConstructor
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {
    private final JPAQueryFactory queryFactory;


    @Override
    public List<Product> getLowestProductsByCategory(ProductQueryParam productQueryParam) {
        QProduct subProduct = new QProduct("subProduct");

        return queryFactory.select(product)
                .from(product)
                .join(product.brand, brand).fetchJoin()
                .join(product.category, category).fetchJoin()
                .where(Expressions.list(product.category.seq, product.price).in(
                        JPAExpressions
                                .select(category.seq, subProduct.price.min())
                                .from(subProduct)
                                .join(subProduct.category, category)
                                .where(categoryNameEq(productQueryParam.categoryName()))
                                .groupBy(category.seq)
                ))
                .fetch();
    }

    @Override
    public List<Product> getHighestProductsByCategory(ProductQueryParam productQueryParam) {
        QProduct subProduct = new QProduct("subProduct");

        return queryFactory.select(product)
                .from(product)
                .join(product.brand, brand).fetchJoin()
                .join(product.category, category).fetchJoin()
                .where(Expressions.list(product.category.seq, product.price).in(
                        JPAExpressions
                                .select(category.seq, subProduct.price.max())
                                .from(subProduct)
                                .join(subProduct.category, category)
                                .where(categoryNameEq(productQueryParam.categoryName()))
                                .groupBy(category.seq)
                ))
                .fetch();
    }

    @Override
    public List<Product> getLowestProductsByBrandAndCategory() {
        QProduct subProduct = new QProduct("subProduct");

        return queryFactory.select(product)
                .from(product)
                .join(product.brand, brand).fetchJoin()
                .join(product.category, category).fetchJoin()
                .where(Expressions.list(brand.seq, category.seq, product.price).in(
                        JPAExpressions
                                .select(brand.seq, category.seq, subProduct.price.min())
                                .from(subProduct)
                                .join(subProduct.category, category)
                                .join(subProduct.brand, brand)
                                .groupBy(brand.seq, category.seq)
                ))
                .fetch();
    }

    private static BooleanExpression categoryNameEq(String categoryName){
        if(categoryName == null) return null;
        return category.name.eq(categoryName);
    }
}
