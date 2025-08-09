package ru.yandex.practicum.smarthometech.commerce.cart.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.smarthometech.commerce.api.client.ShoppingCartClient;
import ru.yandex.practicum.smarthometech.commerce.api.dto.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.smarthometech.commerce.api.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.smarthometech.commerce.cart.application.ShoppingCartMapper;
import ru.yandex.practicum.smarthometech.commerce.cart.application.ShoppingCartService;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/shopping-cart")
@RequiredArgsConstructor
public class ShoppingCartController implements ShoppingCartClient {

    private final ShoppingCartService cartService;
    private final ShoppingCartMapper cartMapper;

    @Override
    @GetMapping
    public ShoppingCartDto getCart(@RequestParam String username) {
        return cartMapper.toDto(cartService.getOrCreateCart(username));
    }

    @Override
    @PutMapping
    public ShoppingCartDto addItem(@RequestParam String username, @RequestBody Map<String, Long> products) {
        Map<UUID, Long> productsWithUuid = products.entrySet().stream()
            .collect(Collectors.toMap(entry -> UUID.fromString(entry.getKey()), Map.Entry::getValue));
        return cartService.addProducts(username, productsWithUuid);
    }

    @Override
    @PostMapping("/remove")
    public ShoppingCartDto removeItem(@RequestParam String username, @RequestBody List<UUID> productIds) {
        List<UUID> uuids = productIds.stream().toList();
        return cartService.removeProducts(username, uuids);
    }

    @Override
    @PostMapping("/change-quantity")
    public ShoppingCartDto changeItemQuantity(@RequestParam String username, @RequestBody ChangeProductQuantityRequest request) {
        return cartService.changeProductQuantity(username, request);
    }

    @Override
    @DeleteMapping
    public void deactivateCart(@RequestParam String username) {
        cartService.deactivateCart(username);
    }
}