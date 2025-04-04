package com.musinsa.product.category;

import com.musinsa.common.exception.CustomException;
import com.musinsa.core.domain.category.dao.CategoryRepository;
import com.musinsa.core.domain.category.entity.Category;
import com.musinsa.product.category.dto.CategoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Transactional
    public Long create(CategoryDto categoryDto) {
        String name = categoryDto.name();
        boolean nameExists = categoryRepository.existsByName(name);
        if(nameExists) throw CustomException.badRequest("이미 존재하는 카테고리 명입니다.");

        Category category = Category.builder()
                .name(name)
                .build();

        Category savedCategory = categoryRepository.save(category);

        return savedCategory.getSeq();
    }
}
