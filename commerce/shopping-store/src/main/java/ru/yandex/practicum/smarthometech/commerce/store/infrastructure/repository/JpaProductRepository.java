package ru.yandex.practicum.smarthometech.commerce.store.infrastructure.repository;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.smarthometech.commerce.api.dto.store.ProductCategory;
import ru.yandex.practicum.smarthometech.commerce.api.dto.store.ProductState;
import ru.yandex.practicum.smarthometech.commerce.store.domain.entity.Product;
import ru.yandex.practicum.smarthometech.commerce.store.domain.repository.ProductRepository;

@Repository
public interface JpaProductRepository extends JpaRepository<Product, UUID>, ProductRepository {

    @Override
    default Page<Product> findByCategoryAndState(ProductCategory category, ProductState state, Pageable pageable) {
        return findByProductCategoryAndProductState(category, state, pageable);
    }

    Page<Product> findByProductCategoryAndProductState(ProductCategory category, ProductState state, Pageable pageable);

}
