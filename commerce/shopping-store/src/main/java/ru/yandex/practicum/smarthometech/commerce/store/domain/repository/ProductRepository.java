package ru.yandex.practicum.smarthometech.commerce.store.domain.repository;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.smarthometech.commerce.api.dto.store.ProductCategory;
import ru.yandex.practicum.smarthometech.commerce.api.dto.store.ProductState;
import ru.yandex.practicum.smarthometech.commerce.store.domain.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    Page<Product> findByProductCategoryAndProductState(ProductCategory category, ProductState state, Pageable pageable);
}
