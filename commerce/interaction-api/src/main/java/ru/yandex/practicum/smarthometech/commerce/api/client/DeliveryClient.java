package ru.yandex.practicum.smarthometech.commerce.api.client;

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
    DeliveryDto planDelivery(@RequestBody DeliveryDto delivery);

    @PostMapping("/successful")
    UUID setDeliverySuccessful(@RequestBody UUID deliveryId);

    @PostMapping("/failed")
    UUID setDeliveryFailed(@RequestBody UUID deliveryId);

    @PostMapping("/picked")
    UUID setDeliveryPickedUp(@RequestBody UUID deliveryId);

    @PostMapping("/cost")
    BigDecimal getDeliveryCost(@RequestBody OrderDto order);
}
