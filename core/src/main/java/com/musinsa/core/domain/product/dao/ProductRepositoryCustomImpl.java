package com.musinsa.core.domain.product.dao;

import com.musinsa.core.domain.product.entity.Product;
import com.musinsa.core.domain.product.entity.QProduct;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.musinsa.core.domain.brand.entity.QBrand.brand;
import static com.musinsa.core.domain.category.entity.QCategory.category;
import static com.musinsa.core.domain.product.entity.QProduct.product;

@RequiredArgsConstructor
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {
    private final JPAQueryFactory queryFactory;


    @Override
    public List<Product> getLowestProductsByCategory() {
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
                                .groupBy(category.seq)
                ))
                .fetch();
    }
}
