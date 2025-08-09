package ru.yandex.practicum.smarthometech.commerce.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.smarthometech.commerce.api.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.smarthometech.commerce.api.dto.warehouse.AddressDto;
import ru.yandex.practicum.smarthometech.commerce.api.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.smarthometech.commerce.api.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.smarthometech.commerce.api.dto.cart.ShoppingCartDto;

@FeignClient(name = "warehouse", path = "/api/v1/warehouse")
public interface WarehouseClient {

    @PostMapping("/check")
    BookedProductsDto checkProductQuantity(@RequestBody ShoppingCartDto shoppingCart);

    @PostMapping("/add")
    void increaseProductQuantity(@RequestBody AddProductToWarehouseRequest product);

    @GetMapping("/address")
    AddressDto getWarehouseAddress();

    @PutMapping
    void addNewProduct(@RequestBody NewProductInWarehouseRequest product);

}
