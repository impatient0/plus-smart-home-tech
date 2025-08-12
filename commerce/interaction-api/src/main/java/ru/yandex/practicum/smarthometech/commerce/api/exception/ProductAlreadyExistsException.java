package ru.yandex.practicum.smarthometech.commerce.api.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(value = HttpStatus.CONFLICT)
@Getter
public class ProductAlreadyExistsException extends RuntimeException {

    private final UUID productId;

    public ProductAlreadyExistsException(UUID productId) {
        super(String.format("Product with ID '%s' already exists in warehouse.", productId));
        this.productId = productId;
    }
}
