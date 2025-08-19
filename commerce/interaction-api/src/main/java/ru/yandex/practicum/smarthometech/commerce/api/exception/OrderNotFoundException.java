package ru.yandex.practicum.smarthometech.commerce.api.exception;

import java.util.UUID;
import lombok.Getter;

@Getter
public class OrderNotFoundException extends RuntimeException {

    private final UUID orderId;

    public OrderNotFoundException(UUID orderId) {
        super(String.format("Order with ID %s not found", orderId));
        this.orderId = orderId;
    }
}
