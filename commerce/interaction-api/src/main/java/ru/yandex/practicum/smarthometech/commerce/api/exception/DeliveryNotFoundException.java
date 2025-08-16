package ru.yandex.practicum.smarthometech.commerce.api.exception;

import java.util.UUID;
import lombok.Getter;

@Getter
public class DeliveryNotFoundException extends RuntimeException {

    private final UUID deliveryId;

    public DeliveryNotFoundException(UUID deliveryId) {
        super("Delivery with ID " + deliveryId + " not found.");
        this.deliveryId = deliveryId;
    }
}
