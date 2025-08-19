package ru.yandex.practicum.smarthometech.commerce.delivery.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.smarthometech.commerce.delivery.domain.Delivery;

import java.util.Optional;
import java.util.UUID;

public interface JpaDeliveryRepositoryInterface extends JpaRepository<Delivery, UUID> {

    Optional<Delivery> findByOrderId(UUID orderId);
}