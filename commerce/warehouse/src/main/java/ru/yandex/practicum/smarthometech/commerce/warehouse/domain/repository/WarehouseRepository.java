package ru.yandex.practicum.smarthometech.commerce.warehouse.domain.repository;

import ru.yandex.practicum.smarthometech.commerce.warehouse.domain.entity.WarehouseItem;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface WarehouseRepository {

    Optional<WarehouseItem> findById(UUID productId);

    List<WarehouseItem> findAllById(Iterable<UUID> productIds);

    WarehouseItem save(WarehouseItem warehouseItem);

    boolean existsById(UUID productId);
}