package ru.yandex.practicum.smarthometech.commerce.warehouse.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.smarthometech.commerce.warehouse.domain.OrderBooking;

import java.util.UUID;

public interface JpaOrderBookingRepositoryInterface extends JpaRepository<OrderBooking, UUID> {
}