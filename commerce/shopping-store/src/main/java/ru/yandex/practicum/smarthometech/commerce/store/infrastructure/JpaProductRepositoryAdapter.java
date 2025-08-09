package ru.yandex.practicum.smarthometech.commerce.store.infrastructure;

import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.smarthometech.commerce.store.domain.ProductCategory;
import ru.yandex.practicum.smarthometech.commerce.store.domain.Product;
import ru.yandex.practicum.smarthometech.commerce.store.domain.ProductRepository;

@Repository
@RequiredArgsConstructor
public class JpaProductRepositoryAdapter implements ProductRepository {

    private final JpaProductRepositoryInterface jpaRepository;

    @Override
    public Optional<Product> findById(UUID productId) {
        return jpaRepository.findById(productId);
    }

    @Override
    public Product save(Product product) {
        return jpaRepository.saveAndFlush(product);
    }

    @Override
    public Page<Product> findByCategory(ProductCategory category, Pageable pageable) {
        return jpaRepository.findByProductCategory(category, pageable);
    }

    @Override
    public boolean existsById(UUID productId) {
        return jpaRepository.existsById(productId);
    }
}