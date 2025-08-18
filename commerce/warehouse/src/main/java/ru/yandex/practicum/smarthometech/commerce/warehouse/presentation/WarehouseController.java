package ru.yandex.practicum.smarthometech.commerce.warehouse.presentation;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.smarthometech.commerce.api.client.WarehouseClient;
import ru.yandex.practicum.smarthometech.commerce.api.dto.warehouse.*;
import ru.yandex.practicum.smarthometech.commerce.api.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.smarthometech.commerce.warehouse.application.WarehouseService;

@RestController
@RequestMapping("/api/v1/warehouse")
@RequiredArgsConstructor
@Validated
public class WarehouseController implements WarehouseClient {

    private final WarehouseService warehouseService;

    @Override
    @PutMapping
    public void addNewProduct(NewProductInWarehouseRequest request) {
        warehouseService.addNewProduct(request);
    }

    @Override
    @PostMapping("/assembly")
    public BookedProductsDto assembleOrder(@RequestBody AssemblyProductsForOrderRequest request) {
        return warehouseService.assembleOrder(request);
    }

    @Override
    @PostMapping("/shipped")
    public void markAsShippedToDelivery(@RequestBody ShippedToDeliveryRequest request) {
        warehouseService.markAsShippedForDelivery(request);
    }

    @Override
    @PostMapping("/return")
    public void acceptProductReturn(@RequestBody Map<String, Long> productsToReturn) {
        warehouseService.acceptProductReturn(productsToReturn);
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