package ru.yandex.practicum.smarthometech.commerce.order.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {

    Optional<Order> findById(UUID orderId);

    List<Order> findByUsername(String username);

    Order save(Order order);
}