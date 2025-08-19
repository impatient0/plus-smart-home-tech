package ru.yandex.practicum.smarthometech.commerce.api.exception;

import java.util.UUID;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.CONFLICT)
public class OrderBookingAlreadyExistsException extends RuntimeException {

    private final UUID orderId;

    public OrderBookingAlreadyExistsException(UUID orderId) {
        super(String.format("Order booking for order ID '%s' already exists.".formatted(orderId)));
        this.orderId = orderId;
    }
}
