package ru.yandex.practicum.smarthometech.commerce.order.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.smarthometech.commerce.api.dto.common.ApiErrorDto;
import ru.yandex.practicum.smarthometech.commerce.api.exception.PaymentClientException;

import ru.yandex.practicum.smarthometech.commerce.api.utility.ErrorParser;

@Slf4j
public class PaymentErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {
        ApiErrorDto errorDto = ErrorParser.parseErrorDto(response).orElse(null);
        String message = errorDto != null ? errorDto.getMessage() : "No details provided";
        String errorCode = errorDto != null ? errorDto.getErrorCode() : "UNKNOWN_ERROR";

        log.error("Error from Payment service. Status: {}, Method: {}, ErrorCode: {}, Message: {}",
            response.status(), methodKey, errorCode, message);

        return new PaymentClientException("Error from Payment service: " + message);
    }
}