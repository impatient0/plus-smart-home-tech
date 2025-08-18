package ru.yandex.practicum.smarthometech.commerce.cart.infrastructure;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import ru.yandex.practicum.smarthometech.commerce.api.dto.common.ApiErrorDto;
import ru.yandex.practicum.smarthometech.commerce.api.exception.InsufficientQuantityException;
import ru.yandex.practicum.smarthometech.commerce.api.exception.ProductNotFoundException;
import ru.yandex.practicum.smarthometech.commerce.api.exception.WarehouseClientException;

import java.util.UUID;
import ru.yandex.practicum.smarthometech.commerce.api.utility.ErrorParser;

@Slf4j
public class WarehouseErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        ApiErrorDto errorDto = ErrorParser.parseErrorDto(response).orElse(null);
        String userMessage = errorDto != null  ? errorDto.getMessage() : "No details provided";

        HttpStatus status = HttpStatus.valueOf(response.status());

        return switch (status) {
            case NOT_FOUND -> {
                log.warn("Warehouse service returned 404. Method: {}. Reason: {}", methodKey, userMessage);
                UUID productId = ErrorParser.extractUuid(errorDto, "productId").orElse(null);
                yield new ProductNotFoundException(productId);
            }
            case BAD_REQUEST -> {
                log.warn("Warehouse service returned 400. Method: {}. Reason: {}", methodKey, userMessage);
                if (userMessage.toLowerCase().contains("insufficient stock")) {
                    UUID productId = ErrorParser.extractUuid(errorDto, "productId").orElse(null);
                    Long requested = ErrorParser.extractLong(errorDto, "requestedQuantity").orElse(0L);
                    Long available = ErrorParser.extractLong(errorDto, "availableQuantity").orElse(0L);
                    yield new InsufficientQuantityException(productId, requested, available);
                }
                yield new WarehouseClientException("Bad request to warehouse service: " + userMessage);
            }
            default -> {
                log.error("Warehouse service returned an unexpected error. Status: {}. Method: {}.", status, methodKey);
                yield new WarehouseClientException("Generic error communicating with Warehouse service. Status: " + status);
            }
        };
    }
}