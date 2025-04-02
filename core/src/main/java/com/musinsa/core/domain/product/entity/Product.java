package com.musinsa.core.domain.product.entity;


import com.musinsa.core.BaseTime;
import com.musinsa.core.domain.brand.entity.Brand;
import com.musinsa.core.domain.category.entity.Category;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class Product extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    @Column(nullable = false)
    private long price;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "category_seq", referencedColumnName = "seq", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "brand_seq", referencedColumnName = "seq", nullable = false)
    private Brand brand;

}
