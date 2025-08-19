package ru.yandex.practicum.smarthometech.commerce.warehouse.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.smarthometech.commerce.warehouse.domain.OrderBooking;
import ru.yandex.practicum.smarthometech.commerce.warehouse.domain.OrderBookingRepository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaOrderBookingRepositoryAdapter implements OrderBookingRepository {

    private final JpaOrderBookingRepositoryInterface jpaRepository;

    @Override
    public Optional<OrderBooking> findById(UUID orderId) {
        return jpaRepository.findById(orderId);
    }

    @Override
    public OrderBooking save(OrderBooking orderBooking) {
        return jpaRepository.saveAndFlush(orderBooking);
    }

    @Override
    public boolean existsById(UUID orderId) {
        return jpaRepository.existsById(orderId);
    }
}