package ru.yandex.practicum.smarthometech.commerce.order.infrastructure;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import ru.yandex.practicum.smarthometech.commerce.api.dto.common.ApiErrorDto;
import ru.yandex.practicum.smarthometech.commerce.api.exception.*;

import ru.yandex.practicum.smarthometech.commerce.api.utility.ErrorParser;

@Slf4j
public class WarehouseErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        ApiErrorDto errorDto = ErrorParser.parseErrorDto(response).orElse(null);
        String message = errorDto != null ? errorDto.getMessage() : "No details provided";
        String errorCode = errorDto != null ? errorDto.getErrorCode() : "UNKNOWN_ERROR";

        HttpStatus status = HttpStatus.valueOf(response.status());

        return switch (errorCode) {
            case "PRODUCT_NOT_FOUND" -> new ProductNotFoundException(ErrorParser.extractUuid(errorDto, "productId").orElse(null));
            case "INSUFFICIENT_QUANTITY" -> new InsufficientQuantityException(
                ErrorParser.extractUuid(errorDto, "productId").orElse(null),
                ErrorParser.extractLong(errorDto, "requestedQuantity").orElse(0L),
                ErrorParser.extractLong(errorDto, "availableQuantity").orElse(0L)
            );
            case "ORDER_BOOKING_ALREADY_EXISTS" -> new OrderBookingAlreadyExistsException(ErrorParser.extractUuid(errorDto, "orderId").orElse(null));
            default -> {
                log.error("Unhandled error from Warehouse. Status: {}, Method: {}, ErrorCode: {}, Message: {}", status, methodKey, errorCode, message);
                yield new WarehouseClientException("Generic error from Warehouse service: " + message);
            }
        };
    }
}