package ru.yandex.practicum.smarthometech.commerce.delivery.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.smarthometech.commerce.api.client.DeliveryClient;
import ru.yandex.practicum.smarthometech.commerce.api.dto.delivery.DeliveryDto;
import ru.yandex.practicum.smarthometech.commerce.api.dto.order.OrderDto;
import ru.yandex.practicum.smarthometech.commerce.delivery.application.DeliveryService;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/delivery")
@RequiredArgsConstructor
@Validated
public class DeliveryController implements DeliveryClient {

    private final DeliveryService deliveryService;

    @Override
    @PutMapping
    public DeliveryDto planDelivery(@RequestBody DeliveryDto deliveryDto) {
        return deliveryService.planDelivery(deliveryDto);
    }

    @Override
    @PostMapping("/successful")
    public void setDeliverySuccessful(@RequestBody UUID deliveryId) {
        deliveryService.setDeliverySuccessful(deliveryId);
    }

    @Override
    @PostMapping("/failed")
    public void setDeliveryFailed(@RequestBody UUID deliveryId) {
        deliveryService.setDeliveryFailed(deliveryId);
    }

    @Override
    @PostMapping("/picked")
    public void setDeliveryPickedUp(@RequestBody UUID deliveryId) {
        deliveryService.setDeliveryPickedUp(deliveryId);
    }

    @Override
    @PostMapping("/cost")
    public BigDecimal getDeliveryCost(@RequestBody OrderDto order) {
        return deliveryService.calculateDeliveryCost(order);
    }
}