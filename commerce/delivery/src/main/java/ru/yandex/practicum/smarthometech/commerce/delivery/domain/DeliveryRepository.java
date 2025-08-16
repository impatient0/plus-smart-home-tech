package ru.yandex.practicum.smarthometech.commerce.delivery.domain;

import java.util.Optional;
import java.util.UUID;

public interface DeliveryRepository {

    Optional<Delivery> findById(UUID deliveryId);

    Optional<Delivery> findByOrderId(UUID orderId);

    Delivery save(Delivery delivery);

}