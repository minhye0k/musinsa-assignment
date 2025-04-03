package com.musinsa.product.brand;

import com.musinsa.common.exception.CustomException;
import com.musinsa.core.domain.product.entity.Product;
import com.musinsa.product.brand.dto.BrandDto;
import com.musinsa.core.domain.brand.dao.BrandRepository;
import com.musinsa.core.domain.brand.entity.Brand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BrandService {
    private final BrandRepository brandRepository;

    @Transactional
    public Long create(BrandDto brandDto) {
        String name = brandDto.name();
        boolean exists = brandRepository.existsByName(name);
        if (exists) throw CustomException.badRequest("이미 존재하는 브랜드 명입니다.");

        Brand brand = Brand.builder()
                .name(name)
                .build();

        Brand savedBrand = brandRepository.save(brand);
        return savedBrand.getSeq();
    }

    @Transactional
    public String update(Long seq, BrandDto brandDto) {
        Brand brand = brandRepository.findById(seq)
                .orElseThrow(() -> CustomException.notFound("해당 브랜드를 찾을 수 없습니다."));

        final String name = brandDto.name();
        boolean exists = brandRepository.existsByName(name);
        if (exists) throw CustomException.badRequest("이미 존재하는 브랜드 명입니다.");

        brand.update(name);

        return brand.getName();
    }

    @Transactional
    public String delete(Long seq) {
        Brand brand = brandRepository.findBySeqWithProducts(seq)
                .orElseThrow(() -> CustomException.notFound("해당 브랜드를 찾을 수 없습니다."));

        if(brand.hasProduct()){
            throw CustomException.badRequest("상품이 있는 브랜드는 삭제할 수 없습니다.");
        }
        brandRepository.delete(brand);
        return brand.getName();
    }
}
