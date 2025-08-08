package ru.yandex.practicum.smarthometech.commerce.api.client;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.smarthometech.commerce.api.dto.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.smarthometech.commerce.api.dto.cart.ShoppingCartDto;

@FeignClient(name = "shopping-cart", path = "/api/v1/shopping-cart")
public interface ShoppingCartClient {

    @GetMapping
    ShoppingCartDto getCart(@RequestParam String username);

    @PutMapping
    ShoppingCartDto addItem(@RequestParam String username, @RequestBody Map<String, Long> items);

    @DeleteMapping
    void deactivateCart(@RequestParam String username);

    @PostMapping("/remove")
    ShoppingCartDto removeItem(@RequestParam String username, @RequestBody List<UUID> items);

    @PostMapping("/change-quantity")
    ShoppingCartDto changeItemQuantity(@RequestParam String username, @RequestBody
        ChangeProductQuantityRequest request);

}
