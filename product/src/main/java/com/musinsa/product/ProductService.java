package com.musinsa.product;

import com.musinsa.core.domain.category.dao.CategoryRepository;
import com.musinsa.core.domain.category.entity.Category;
import com.musinsa.core.domain.product.dao.ProductRepository;
import com.musinsa.core.domain.product.entity.Product;
import com.musinsa.product.dto.ProductDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {
    private final ProductRepository productRepository;

    public List<ProductDto> getLowestProductsByCategory() {
        List<Product> products = productRepository.getLowestProductsByCategory();

        List<Product> dedupedProducts = dedupeByCategory(products);
        return dedupedProducts.stream().map(ProductDto::from).toList();
    }

    private List<Product> dedupeByCategory(List<Product> products) {
        Map<Long, Product> productByCategory = new HashMap<>();

        for (Product product : products) {
            productByCategory.putIfAbsent(product.getCategory().getSeq(), product);
        }
        return productByCategory.values().stream().toList();
    }


}
