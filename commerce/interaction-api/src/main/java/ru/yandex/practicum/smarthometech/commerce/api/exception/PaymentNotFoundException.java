package ru.yandex.practicum.smarthometech.commerce.api.exception;

import java.util.UUID;

public class PaymentNotFoundException extends RuntimeException {
    public PaymentNotFoundException(UUID paymentId) {
        super(String.format("Payment with ID %s not found", paymentId));
    }
}
