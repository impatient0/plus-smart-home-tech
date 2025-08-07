package ru.yandex.practicum.smarthometech.commerce.cart.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.smarthometech.commerce.cart.domain.enums.CartStatus;
import ru.yandex.practicum.smarthometech.commerce.cart.domain.entity.ShoppingCart;

import java.util.Optional;
import java.util.UUID;

public interface JpaShoppingCartRepositoryInterface extends JpaRepository<ShoppingCart, UUID> {

    Optional<ShoppingCart> findByUsernameAndStatus(String username, CartStatus status);

}