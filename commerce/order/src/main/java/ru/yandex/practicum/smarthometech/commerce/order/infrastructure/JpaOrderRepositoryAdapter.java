package ru.yandex.practicum.smarthometech.commerce.order.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.smarthometech.commerce.order.domain.Order;
import ru.yandex.practicum.smarthometech.commerce.order.domain.OrderRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaOrderRepositoryAdapter implements OrderRepository {

    private final JpaOrderRepositoryInterface jpaRepository;

    @Override
    public Optional<Order> findById(UUID orderId) {
        return jpaRepository.findById(orderId);
    }

    @Override
    public List<Order> findByUsername(String username) {
        return jpaRepository.findByUsername(username);
    }

    @Override
    public Order save(Order order) {
        return jpaRepository.saveAndFlush(order);
    }
}