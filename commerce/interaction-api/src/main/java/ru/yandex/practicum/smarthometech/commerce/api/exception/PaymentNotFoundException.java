package ru.yandex.practicum.smarthometech.commerce.api.exception;

import java.util.UUID;
import lombok.Getter;

@Getter
public class PaymentNotFoundException extends RuntimeException {

    private final UUID paymentId;

    public PaymentNotFoundException(UUID paymentId) {
        super(String.format("Payment with ID %s not found", paymentId));
        this.paymentId = paymentId;
    }
}
