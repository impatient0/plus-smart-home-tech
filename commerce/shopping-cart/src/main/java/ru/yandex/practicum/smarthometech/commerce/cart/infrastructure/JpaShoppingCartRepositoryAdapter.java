package ru.yandex.practicum.smarthometech.commerce.cart.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.smarthometech.commerce.cart.domain.CartStatus;
import ru.yandex.practicum.smarthometech.commerce.cart.domain.ShoppingCart;
import ru.yandex.practicum.smarthometech.commerce.cart.domain.ShoppingCartRepository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaShoppingCartRepositoryAdapter implements ShoppingCartRepository {

    private final JpaShoppingCartRepositoryInterface jpaRepository;

    @Override
    public Optional<ShoppingCart> findActiveByUsername(String username) {
        return jpaRepository.findByUsernameAndStatus(username, CartStatus.ACTIVE);
    }

    @Override
    public Optional<ShoppingCart> findById(UUID shoppingCartId) {
        return jpaRepository.findById(shoppingCartId);
    }

    @Override
    public ShoppingCart save(ShoppingCart cart) {
        return jpaRepository.saveAndFlush(cart);
    }
}