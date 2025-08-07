package ru.yandex.practicum.smarthometech.commerce.warehouse.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.smarthometech.commerce.api.client.WarehouseClient;
import ru.yandex.practicum.smarthometech.commerce.api.dto.warehouse.*;
import ru.yandex.practicum.smarthometech.commerce.warehouse.application.service.WarehouseService;

@RestController
@RequiredArgsConstructor
public class WarehouseController implements WarehouseClient {

    private final WarehouseService warehouseService;

    @Override
    @PutMapping
    public void addNewProduct(NewProductInWarehouseRequest request) {
        warehouseService.addNewProduct(request);
    }

    @Override
    @PostMapping("/add")
    public void increaseProductQuantity(AddProductToWarehouseRequest request) {
        warehouseService.increaseProductQuantity(request);
    }

    @Override
    @PostMapping("/check")
    public BookedProductsDto checkProductQuantity(ShoppingCartDto shoppingCart) {
        return warehouseService.checkProductQuantity(shoppingCart);
    }

    @Override
    @GetMapping("/address")
    public AddressDto getWarehouseAddress() {
        return warehouseService.getWarehouseAddress();
    }
}