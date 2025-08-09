package ru.yandex.practicum.smarthometech.commerce.warehouse.infrastructure;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.smarthometech.commerce.warehouse.domain.WarehouseItem;

public interface JpaWarehouseRepositoryInterface extends JpaRepository<WarehouseItem, UUID> {
}