package ru.yandex.practicum.smarthometech.commerce.order.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.smarthometech.commerce.api.dto.common.ApiErrorDto;
import ru.yandex.practicum.smarthometech.commerce.api.exception.PaymentClientException; // A new base exception

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@Slf4j
public class PaymentErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {
        ApiErrorDto errorDto = parseErrorDto(response).orElse(null);
        String message = errorDto != null ? errorDto.getMessage() : "No details provided";
        String errorCode = errorDto != null ? errorDto.getErrorCode() : "UNKNOWN_ERROR";

        log.error("Error from Payment service. Status: {}, Method: {}, ErrorCode: {}, Message: {}",
            response.status(), methodKey, errorCode, message);

        return new PaymentClientException("Error from Payment service: " + message);
    }

    private Optional<ApiErrorDto> parseErrorDto(Response response) {
        if (response.body() == null) return Optional.empty();
        try (InputStream bodyIs = response.body().asInputStream()) {
            return Optional.of(objectMapper.readValue(bodyIs, ApiErrorDto.class));
        } catch (IOException e) {
            log.error("Failed to decode error response body from Payment service", e);
            return Optional.empty();
        }
    }
}