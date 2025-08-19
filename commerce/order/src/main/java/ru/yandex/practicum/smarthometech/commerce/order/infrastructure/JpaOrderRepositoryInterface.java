package ru.yandex.practicum.smarthometech.commerce.order.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.smarthometech.commerce.order.domain.Order;

import java.util.List;
import java.util.UUID;

public interface JpaOrderRepositoryInterface extends JpaRepository<Order, UUID> {

    List<Order> findByUsername(String username);
}