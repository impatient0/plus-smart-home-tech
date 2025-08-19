package ru.yandex.practicum.smarthometech.commerce.api.exception;

import java.util.UUID;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class OrderCreationException extends RuntimeException {

    private final UUID cartId;
    private final String errorMessage;

    public OrderCreationException(UUID cartId, String errorMessage) {
        super(String.format("Failed to create order for cartId %s due to unexpected error: %s", cartId, errorMessage));
        this.cartId = cartId;
        this.errorMessage = errorMessage;
    }
}
