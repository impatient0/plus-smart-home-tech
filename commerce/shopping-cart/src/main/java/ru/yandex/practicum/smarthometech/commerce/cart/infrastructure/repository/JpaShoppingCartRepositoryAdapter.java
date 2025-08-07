package ru.yandex.practicum.smarthometech.commerce.cart.infrastructure.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.smarthometech.commerce.cart.domain.enums.CartStatus;
import ru.yandex.practicum.smarthometech.commerce.cart.domain.entity.ShoppingCart;
import ru.yandex.practicum.smarthometech.commerce.cart.domain.repository.ShoppingCartRepository;

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