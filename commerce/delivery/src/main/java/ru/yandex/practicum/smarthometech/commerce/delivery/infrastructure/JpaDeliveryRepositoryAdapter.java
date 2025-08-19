package ru.yandex.practicum.smarthometech.commerce.delivery.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.smarthometech.commerce.delivery.domain.Delivery;
import ru.yandex.practicum.smarthometech.commerce.delivery.domain.DeliveryRepository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaDeliveryRepositoryAdapter implements DeliveryRepository {

    private final JpaDeliveryRepositoryInterface jpaRepository;

    @Override
    public Optional<Delivery> findById(UUID deliveryId) {
        return jpaRepository.findById(deliveryId);
    }

    @Override
    public Optional<Delivery> findByOrderId(UUID orderId) {
        return jpaRepository.findByOrderId(orderId);
    }

    @Override
    public Delivery save(Delivery delivery) {
        return jpaRepository.saveAndFlush(delivery);
    }
}