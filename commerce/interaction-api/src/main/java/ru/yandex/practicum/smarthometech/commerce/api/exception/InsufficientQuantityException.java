package ru.yandex.practicum.smarthometech.commerce.api.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(value = HttpStatus.CONFLICT)
@Getter
public class InsufficientQuantityException extends RuntimeException {

    private final UUID productId;
    private final Long requestedQuantity;
    private final Long availableQuantity;

    public InsufficientQuantityException(UUID productId, Long requestedQuantity, Long availableQuantity) {
        super(String.format("Insufficient quantity for product with ID '%s'. Requested: %d, Available: %d",
                productId, requestedQuantity, availableQuantity));
        this.productId = productId;
        this.requestedQuantity = requestedQuantity;
        this.availableQuantity = availableQuantity;
    }
}
