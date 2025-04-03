package com.musinsa.core.domain.brand.dao;

import com.musinsa.core.domain.brand.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
    boolean existsByName(String name);

    Optional<Brand> findByName(String brand);

    @Query("SELECT b FROM Brand b LEFT JOIN FETCH b.products WHERE b.seq = :seq")
    Optional<Brand> findBySeqWithProducts(@Param("seq") Long seq);
}
