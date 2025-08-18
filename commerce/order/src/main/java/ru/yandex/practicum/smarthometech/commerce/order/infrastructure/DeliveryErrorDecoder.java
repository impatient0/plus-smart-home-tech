package ru.yandex.practicum.smarthometech.commerce.order.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.smarthometech.commerce.api.dto.common.ApiErrorDto;
import ru.yandex.practicum.smarthometech.commerce.api.exception.DeliveryClientException;
import ru.yandex.practicum.smarthometech.commerce.api.exception.DeliveryNotFoundException;

import ru.yandex.practicum.smarthometech.commerce.api.utility.ErrorParser;

@Slf4j
public class DeliveryErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {
        ApiErrorDto errorDto = ErrorParser.parseErrorDto(response).orElse(null);
        String message = errorDto != null ? errorDto.getMessage() : "No details provided";
        String errorCode = errorDto != null ? errorDto.getErrorCode() : "UNKNOWN_ERROR";

        return switch (errorCode) {
            case "DELIVERY_NOT_FOUND" -> new DeliveryNotFoundException(ErrorParser.extractUuid(errorDto, "identifier").orElse(null));
            default -> {
                log.error("Unhandled error from Delivery service. Status: {}, Method: {}, ErrorCode: {}, Message: {}",
                    response.status(), methodKey, errorCode, message);
                yield new DeliveryClientException("Generic error from Delivery service: " + message);
            }
        };
    }
}