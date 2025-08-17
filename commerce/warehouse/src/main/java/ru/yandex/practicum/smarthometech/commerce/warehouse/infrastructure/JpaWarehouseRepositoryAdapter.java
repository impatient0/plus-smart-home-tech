package ru.yandex.practicum.smarthometech.commerce.warehouse.infrastructure;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.smarthometech.commerce.warehouse.domain.WarehouseItem;
import ru.yandex.practicum.smarthometech.commerce.warehouse.domain.WarehouseRepository;

@Repository
@RequiredArgsConstructor
public class JpaWarehouseRepositoryAdapter implements WarehouseRepository {

    private final JpaWarehouseRepositoryInterface jpaRepository;

    @Override
    public Optional<WarehouseItem> findById(UUID productId) {
        return jpaRepository.findById(productId);
    }

    @Override
    public List<WarehouseItem> findAllById(Iterable<UUID> productIds) {
        return jpaRepository.findAllById(productIds);
    }

    @Override
    public WarehouseItem save(WarehouseItem warehouseItem) {
        return jpaRepository.saveAndFlush(warehouseItem);
    }

    @Override
    public List<WarehouseItem> saveAll(Iterable<WarehouseItem> warehouseItems) {
        return jpaRepository.saveAll(warehouseItems);
    }

    @Override
    public boolean existsById(UUID productId) {
        return jpaRepository.existsById(productId);
    }
}