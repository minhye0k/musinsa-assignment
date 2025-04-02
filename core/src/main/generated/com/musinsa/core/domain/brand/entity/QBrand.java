package com.musinsa.core.domain.brand.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBrand is a Querydsl query type for Brand
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBrand extends EntityPathBase<Brand> {

    private static final long serialVersionUID = -1069968133L;

    public static final QBrand brand = new QBrand("brand");

    public final com.musinsa.core.QBaseTime _super = new com.musinsa.core.QBaseTime(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath name = createString("name");

    public final ListPath<com.musinsa.core.domain.product.entity.Product, com.musinsa.core.domain.product.entity.QProduct> products = this.<com.musinsa.core.domain.product.entity.Product, com.musinsa.core.domain.product.entity.QProduct>createList("products", com.musinsa.core.domain.product.entity.Product.class, com.musinsa.core.domain.product.entity.QProduct.class, PathInits.DIRECT2);

    public final NumberPath<Long> seq = createNumber("seq", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QBrand(String variable) {
        super(Brand.class, forVariable(variable));
    }

    public QBrand(Path<? extends Brand> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBrand(PathMetadata metadata) {
        super(Brand.class, metadata);
    }

}

