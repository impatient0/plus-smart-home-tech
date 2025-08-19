package ru.yandex.practicum.smarthometech.commerce.api.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
@Getter
public class BookingNotFoundException extends RuntimeException {

    private final UUID orderId;

    public BookingNotFoundException(UUID orderId) {
        super(String.format("Booking for order with ID '%s' not found.", orderId));
        this.orderId = orderId;
    }
}