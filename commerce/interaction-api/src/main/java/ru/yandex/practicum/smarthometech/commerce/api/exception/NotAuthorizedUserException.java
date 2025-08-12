package ru.yandex.practicum.smarthometech.commerce.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class NotAuthorizedUserException extends RuntimeException {

    public NotAuthorizedUserException() {
        super("User not authorized");
    }
}
