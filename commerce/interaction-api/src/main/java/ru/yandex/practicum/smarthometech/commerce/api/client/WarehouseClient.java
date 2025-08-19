package ru.yandex.practicum.smarthometech.commerce.api.client;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.smarthometech.commerce.api.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.smarthometech.commerce.api.dto.warehouse.AddressDto;
import ru.yandex.practicum.smarthometech.commerce.api.dto.warehouse.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.smarthometech.commerce.api.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.smarthometech.commerce.api.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.smarthometech.commerce.api.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.smarthometech.commerce.api.dto.warehouse.ShippedToDeliveryRequest;

@FeignClient(name = "warehouse", path = "/api/v1/warehouse")
public interface WarehouseClient {

    @PostMapping("/check")
    BookedProductsDto checkProductQuantity(@Valid @RequestBody ShoppingCartDto shoppingCart);

    @PostMapping("/add")
    void increaseProductQuantity(@Valid @RequestBody AddProductToWarehouseRequest product);

    @GetMapping("/address")
    AddressDto getWarehouseAddress();

    @PutMapping
    void addNewProduct(@Valid @RequestBody NewProductInWarehouseRequest product);

    @PostMapping("/assembly")
    BookedProductsDto assembleOrder(@Valid @RequestBody AssemblyProductsForOrderRequest request);

    @PostMapping("/shipped")
    void markAsShippedToDelivery(@Valid @RequestBody ShippedToDeliveryRequest request);

    @PostMapping("/return")
    void acceptProductReturn(@NotEmpty @RequestBody Map<String, Long> productsToReturn);

}