package ru.yandex.practicum.smarthometech.commerce.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class PaymentClientException extends RuntimeException {

    public PaymentClientException(String message) {
        super(message);
    }
}
