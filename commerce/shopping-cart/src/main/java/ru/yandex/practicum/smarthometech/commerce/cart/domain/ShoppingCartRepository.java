package ru.yandex.practicum.smarthometech.commerce.cart.domain;

import java.util.Optional;
import java.util.UUID;

public interface ShoppingCartRepository {

    Optional<ShoppingCart> findActiveByUsername(String username);

    Optional<ShoppingCart> findById(UUID shoppingCartId);

    ShoppingCart save(ShoppingCart cart);

}