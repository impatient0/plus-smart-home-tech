package ru.yandex.practicum.smarthometech.commerce.store.infrastructure.repository;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.smarthometech.commerce.api.dto.store.ProductCategory;
import ru.yandex.practicum.smarthometech.commerce.api.dto.store.ProductState;
import ru.yandex.practicum.smarthometech.commerce.store.domain.entity.Product;

public interface JpaProductRepositoryInterface extends JpaRepository<Product, UUID> {

    Page<Product> findByProductCategoryAndProductState(ProductCategory category, ProductState state, Pageable pageable);

}
