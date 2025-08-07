package ru.yandex.practicum.smarthometech.commerce.store.domain;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepository {

    Optional<Product> findById(UUID productId);
    Product save(Product product);
    Page<Product> findByCategoryAndState(ProductCategory category, ProductState state, Pageable pageable);
    boolean existsById(UUID productId);

}
