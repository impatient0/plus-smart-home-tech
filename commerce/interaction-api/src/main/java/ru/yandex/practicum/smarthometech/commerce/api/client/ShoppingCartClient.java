package ru.yandex.practicum.smarthometech.commerce.api.client;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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
    ShoppingCartDto getCart(@NotBlank @RequestParam String username);

    @PutMapping
    ShoppingCartDto addItem(@NotBlank @RequestParam String username, @NotEmpty @RequestBody Map<String, Long> items);

    @DeleteMapping
    void deactivateCart(@NotBlank @RequestParam String username);

    @PostMapping("/remove")
    ShoppingCartDto removeItem(@NotBlank @RequestParam String username, @NotEmpty @RequestBody List<UUID> items);

    @PostMapping("/change-quantity")
    ShoppingCartDto changeItemQuantity(@NotBlank @RequestParam String username, @Valid @RequestBody
    ChangeProductQuantityRequest request);

}