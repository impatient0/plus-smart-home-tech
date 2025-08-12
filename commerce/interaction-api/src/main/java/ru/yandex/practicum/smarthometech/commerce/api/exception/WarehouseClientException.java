package ru.yandex.practicum.smarthometech.commerce.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class WarehouseClientException extends RuntimeException {

    public WarehouseClientException(String message) {
        super(message);
    }
}
