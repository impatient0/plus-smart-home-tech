package ru.yandex.practicum.smarthometech.commerce.cart.application;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.smarthometech.commerce.api.client.WarehouseClient;
import ru.yandex.practicum.smarthometech.commerce.api.dto.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.smarthometech.commerce.api.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.smarthometech.commerce.api.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.smarthometech.commerce.api.exception.NotAuthorizedUserException;
import ru.yandex.practicum.smarthometech.commerce.cart.domain.CartItem;
import ru.yandex.practicum.smarthometech.commerce.cart.domain.CartStatus;
import ru.yandex.practicum.smarthometech.commerce.cart.domain.ShoppingCart;
import ru.yandex.practicum.smarthometech.commerce.cart.domain.ShoppingCartRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShoppingCartService {

    private final ShoppingCartRepository cartRepository;
    private final WarehouseClient warehouseClient;
    private final ShoppingCartMapper shoppingCartMapper;

    @Transactional
    public ShoppingCart getOrCreateCart(String username) {
        return cartRepository.findActiveByUsername(username)
            .orElseGet(() -> {
                log.info("No active cart found for user '{}'. Creating a new one.", username);
                ShoppingCart newCart = new ShoppingCart();
                newCart.setUsername(username);
                return cartRepository.save(newCart);
            });
    }

    @Transactional
    public ShoppingCartDto addProducts(String username, Map<UUID, Long> productsToAdd) {
        validateUsername(username);

        ShoppingCart cart = getOrCreateCart(username);

        productsToAdd.forEach((productId, quantity) -> {
            cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .ifPresentOrElse(
                    item -> item.setQuantity(item.getQuantity() + quantity),
                    () -> {
                        CartItem newItem = new CartItem();
                        newItem.setProductId(productId);
                        newItem.setQuantity(quantity);
                        cart.addItem(newItem);
                    }
                );
        });

        validateStock(cart);

        ShoppingCart updatedCart = cartRepository.save(cart);
        log.info("Successfully added/updated {} products for user '{}'", productsToAdd.size(), username);
        return shoppingCartMapper.toDto(updatedCart);
    }

    @Transactional
    public ShoppingCartDto removeProducts(String username, List<UUID> productsToRemove) {
        validateUsername(username);

        ShoppingCart cart = getOrCreateCart(username);

        if (productsToRemove == null || productsToRemove.isEmpty()) {
            log.warn("Empty product list provided for removal for user '{}'.", username);
            return shoppingCartMapper.toDto(cart);
        }

        Set<UUID> idsInCart = cart.getItems().stream()
            .map(CartItem::getProductId)
            .collect(Collectors.toSet());

        if (!idsInCart.containsAll(productsToRemove)) {
            List<UUID> nonExistentProducts = new ArrayList<>(productsToRemove);
            nonExistentProducts.removeAll(idsInCart);
            log.error("Attempted to remove product(s) not in the cart for user '{}': {}", username, nonExistentProducts);
            throw new NoProductsInShoppingCartException(username, nonExistentProducts);
        }

        Set<UUID> productsToRemoveSet = new HashSet<>(productsToRemove);
        cart.getItems().removeIf(item -> productsToRemoveSet.contains(item.getProductId()));
        validateStock(cart);

        ShoppingCart updatedCart = cartRepository.save(cart);
        log.info("Successfully removed {} products for user '{}'", productsToRemove.size(), username);
        return shoppingCartMapper.toDto(updatedCart);
    }

    @Transactional
    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request) {
        validateUsername(username);

        ShoppingCart cart = getOrCreateCart(username);

        UUID productId = request.getProductId();
        Long newQuantity = request.getNewQuantity();

        CartItem itemToChange = cart.getItems().stream()
            .filter(item -> item.getProductId().equals(productId)).findFirst().orElseThrow(() -> {
                log.error("Product with ID '{}' not found in shopping cart for user '{}'",
                    productId, username);
                return new NoProductsInShoppingCartException(username, List.of(productId));
            }); itemToChange.setQuantity(newQuantity);

        validateStock(cart);

        ShoppingCart updatedCart = cartRepository.save(cart);
        log.info("Successfully changed quantity for product '{}' to {} for user '{}'", productId,
            newQuantity, username);
        return shoppingCartMapper.toDto(updatedCart);
    }

    @Transactional
    public void deactivateCart(String username) {
        validateUsername(username);

        ShoppingCart cart = getOrCreateCart(username);
        cart.setStatus(CartStatus.DEACTIVATED);

        cartRepository.save(cart);
        log.info("Cart for user '{}' has been deactivated.", username);
    }

    private void validateUsername(String username) {
        if (username == null || username.isBlank()) {
            log.error("Attempted access by an unauthorized user.");
            throw new NotAuthorizedUserException();
        }
    }

    private void validateStock(ShoppingCart cart) {
        log.debug("Validating stock for cartId: {}", cart.getShoppingCartId());

        ShoppingCartDto cartDto = shoppingCartMapper.toDto(cart);

        warehouseClient.checkProductQuantity(cartDto);
        log.info("Stock validation successful for cartId: {}", cart.getShoppingCartId());
    }
}