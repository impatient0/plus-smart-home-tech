package ru.yandex.practicum.smarthometech.commerce.store.infrastructure;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.smarthometech.commerce.store.domain.ProductCategory;
import ru.yandex.practicum.smarthometech.commerce.store.domain.Product;

public interface JpaProductRepositoryInterface extends JpaRepository<Product, UUID> {

    Page<Product> findByProductCategory(ProductCategory category, Pageable pageable);

}
