package ru.yandex.practicum.smarthometech.commerce.api.client;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.smarthometech.commerce.api.dto.store.ProductCategory;
import ru.yandex.practicum.smarthometech.commerce.api.dto.store.ProductDto;
import ru.yandex.practicum.smarthometech.commerce.api.dto.store.QuantityState;

@FeignClient(name = "shopping-store", path = "/api/v1/shopping-store")
public interface ShoppingStoreClient {

    @GetMapping
    Page<ProductDto> getProducts(@NotNull @RequestParam("category") ProductCategory category, Pageable pageable);

    @PutMapping
    ProductDto createNewProduct(@Valid @RequestBody ProductDto product);

    @PostMapping
    ProductDto updateProduct(@Valid @RequestBody ProductDto product);

    @PostMapping("/removeProductFromStore")
    boolean removeProduct(@NotNull @RequestBody UUID productId);

    @PostMapping("/quantityState")
    boolean setStatus(@NotNull @RequestParam("productId") UUID productId,
        @NotNull @RequestParam("quantityState") QuantityState quantityState);

    @GetMapping("/{productId}")
    ProductDto getProduct(@NotNull @PathVariable UUID productId);

}