package ru.yandex.practicum.smarthometech.commerce.cart.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import ru.yandex.practicum.smarthometech.commerce.api.dto.common.ApiErrorDto;
import ru.yandex.practicum.smarthometech.commerce.api.exception.InsufficientQuantityException;
import ru.yandex.practicum.smarthometech.commerce.api.exception.ProductNotFoundException;
import ru.yandex.practicum.smarthometech.commerce.api.exception.WarehouseInteractionException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class WarehouseErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {
        ApiErrorDto errorDto = parseErrorDto(response).orElse(null);
        String userMessage = (errorDto != null && errorDto.getUserMessage() != null) ? errorDto.getUserMessage() : "No details provided";

        HttpStatus status = HttpStatus.valueOf(response.status());

        return switch (status) {
            case NOT_FOUND -> {
                log.warn("Warehouse service returned 404. Method: {}. Reason: {}", methodKey, userMessage);
                UUID productId = extractUuid(errorDto, "productId").orElse(null);
                yield new ProductNotFoundException(productId);
            }
            case BAD_REQUEST -> {
                log.warn("Warehouse service returned 400. Method: {}. Reason: {}", methodKey, userMessage);
                if (userMessage.toLowerCase().contains("insufficient stock")) {
                    UUID productId = extractUuid(errorDto, "productId").orElse(null);
                    Long requested = extractLong(errorDto, "requestedQuantity").orElse(0L);
                    Long available = extractLong(errorDto, "availableQuantity").orElse(0L);
                    yield new InsufficientQuantityException(productId, requested, available);
                }
                yield new WarehouseInteractionException("Bad request to warehouse service: " + userMessage);
            }
            default -> {
                log.error("Warehouse service returned an unexpected error. Status: {}. Method: {}.", status, methodKey);
                yield new WarehouseInteractionException("Generic error communicating with Warehouse service. Status: " + status);
            }
        };
    }

    private Optional<ApiErrorDto> parseErrorDto(Response response) {
        if (response.body() == null) {
            return Optional.empty();
        }
        try (InputStream bodyIs = response.body().asInputStream()) {
            return Optional.of(objectMapper.readValue(bodyIs, ApiErrorDto.class));
        } catch (IOException e) {
            log.error("Failed to decode error response body from Warehouse service", e);
            return Optional.empty();
        }
    }

    private Optional<UUID> extractUuid(ApiErrorDto dto, String key) {
        if (dto == null || dto.getDetails() == null) return Optional.empty();
        return Optional.ofNullable(dto.getDetails().get(key))
            .filter(String.class::isInstance)
            .map(String.class::cast)
            .map(UUID::fromString);
    }

    private Optional<Long> extractLong(ApiErrorDto dto, String key) {
        if (dto == null || dto.getDetails() == null) return Optional.empty();
        return Optional.ofNullable(dto.getDetails().get(key))
            .filter(Number.class::isInstance)
            .map(Number.class::cast)
            .map(Number::longValue);
    }
}