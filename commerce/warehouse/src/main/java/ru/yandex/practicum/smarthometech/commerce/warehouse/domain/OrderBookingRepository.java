package ru.yandex.practicum.smarthometech.commerce.warehouse.domain;

import java.util.Optional;
import java.util.UUID;

public interface OrderBookingRepository {

    Optional<OrderBooking> findById(UUID orderId);

    OrderBooking save(OrderBooking orderBooking);

    boolean existsById(UUID orderId);
}