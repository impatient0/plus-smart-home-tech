package ru.yandex.practicum.smarthometech.commerce.payment.presentation;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.smarthometech.commerce.api.dto.common.ApiErrorDto;

import java.time.OffsetDateTime;
import java.util.Map;
import ru.yandex.practicum.smarthometech.commerce.api.exception.PaymentNotFoundException;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<ApiErrorDto> handlePaymentNotFound(PaymentNotFoundException ex, HttpServletRequest request) {
        log.warn("Handling PaymentNotFoundException: {}", ex.getMessage());

        ApiErrorDto errorDto = new ApiErrorDto()
            .timestamp(OffsetDateTime.now())
            .status(HttpStatus.NOT_FOUND.value())
            .errorCode("PAYMENT_NOT_FOUND")
            .message("The specified payment record could not be found.")
            .path(request.getRequestURI())
            .details(Map.of("paymentId", ex.getPaymentId().toString()));

        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDto> handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("Handling unexpected exception in payment service", ex);

        ApiErrorDto errorDto = new ApiErrorDto()
            .timestamp(OffsetDateTime.now())
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .errorCode("INTERNAL_SERVER_ERROR")
            .message("An unexpected error occurred in the payment service.")
            .path(request.getRequestURI())
            .details(Map.of("originalMessage", ex.getMessage()));

        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}