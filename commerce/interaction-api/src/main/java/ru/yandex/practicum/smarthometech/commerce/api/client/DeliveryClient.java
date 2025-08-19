package ru.yandex.practicum.smarthometech.commerce.api.client;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.smarthometech.commerce.api.dto.delivery.DeliveryDto;
import ru.yandex.practicum.smarthometech.commerce.api.dto.order.OrderDto;

@FeignClient(name = "delivery", path = "/api/v1/delivery")
public interface DeliveryClient {

    @PutMapping
    DeliveryDto planDelivery(@Valid @RequestBody DeliveryDto delivery);

    @PostMapping("/successful")
    void setDeliverySuccessful(@NotNull @RequestBody UUID deliveryId);

    @PostMapping("/failed")
    void setDeliveryFailed(@NotNull @RequestBody UUID deliveryId);

    @PostMapping("/picked")
    void setDeliveryPickedUp(@NotNull @RequestBody UUID deliveryId);

    @PostMapping("/cost")
    BigDecimal getDeliveryCost(@Valid @RequestBody OrderDto order);
}