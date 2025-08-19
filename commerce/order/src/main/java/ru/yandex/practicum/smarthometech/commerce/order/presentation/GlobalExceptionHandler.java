package ru.yandex.practicum.smarthometech.commerce.order.presentation;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.smarthometech.commerce.api.dto.common.ApiErrorDto;
import ru.yandex.practicum.smarthometech.commerce.api.exception.DeliveryClientException;
import ru.yandex.practicum.smarthometech.commerce.api.exception.DeliveryNotFoundException;
import ru.yandex.practicum.smarthometech.commerce.api.exception.OrderBookingAlreadyExistsException;
import ru.yandex.practicum.smarthometech.commerce.api.exception.OrderNotFoundException;
import ru.yandex.practicum.smarthometech.commerce.api.exception.PaymentClientException;
import ru.yandex.practicum.smarthometech.commerce.api.exception.WarehouseClientException;

import java.time.OffsetDateTime;
import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ApiErrorDto> handleOrderNotFound(OrderNotFoundException ex, HttpServletRequest request) {
        log.warn("Handling OrderNotFoundException: {}", ex.getMessage());

        ApiErrorDto errorDto = new ApiErrorDto()
            .timestamp(OffsetDateTime.now())
            .status(HttpStatus.NOT_FOUND.value())
            .errorCode("ORDER_NOT_FOUND")
            .message("The specified order could not be found.")
            .path(request.getRequestURI())
            .details(Map.of("orderId", ex.getOrderId().toString()));

        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(OrderBookingAlreadyExistsException.class)
    public ResponseEntity<ApiErrorDto> handleOrderBookingAlreadyExists(OrderBookingAlreadyExistsException ex, HttpServletRequest request) {
        log.warn("Handling OrderBookingAlreadyExistsException: {}", ex.getMessage());

        ApiErrorDto errorDto = new ApiErrorDto()
            .timestamp(OffsetDateTime.now())
            .status(HttpStatus.CONFLICT.value())
            .errorCode("ORDER_BOOKING_ALREADY_EXISTS")
            .message("An order booking already exists for the specified order.")
            .path(request.getRequestURI())
            .details(Map.of("orderId", ex.getOrderId().toString()));

        return new ResponseEntity<>(errorDto, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(WarehouseClientException.class)
    public ResponseEntity<ApiErrorDto> handleWarehouseClientError(WarehouseClientException ex, HttpServletRequest request) {
        log.warn("Handling downstream WarehouseClientException: {}", ex.getMessage());

        ApiErrorDto errorDto = new ApiErrorDto()
            .timestamp(OffsetDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .errorCode("WAREHOUSE_VALIDATION_ERROR")
            .message(ex.getMessage())
            .path(request.getRequestURI());

        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PaymentClientException.class)
    public ResponseEntity<ApiErrorDto> handlePaymentClientError(PaymentClientException ex, HttpServletRequest request) {
        log.warn("Handling downstream PaymentClientException: {}", ex.getMessage());

        ApiErrorDto errorDto = new ApiErrorDto()
            .timestamp(OffsetDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .errorCode("PAYMENT_PROCESSING_ERROR")
            .message(ex.getMessage())
            .path(request.getRequestURI());

        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DeliveryNotFoundException.class)
    public ResponseEntity<ApiErrorDto> handleDeliveryNotFound(DeliveryNotFoundException ex, HttpServletRequest request) {
        log.warn("Handling DeliveryNotFoundException: {}", ex.getMessage());

        ApiErrorDto errorDto = new ApiErrorDto()
            .timestamp(OffsetDateTime.now())
            .status(HttpStatus.NOT_FOUND.value())
            .errorCode("DELIVERY_NOT_FOUND")
            .message("The specified delivery could not be found.")
            .path(request.getRequestURI())
            .details(Map.of("deliveryId", ex.getDeliveryId().toString()));

        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DeliveryClientException.class)
    public ResponseEntity<ApiErrorDto> handleDeliveryClientError(DeliveryClientException ex, HttpServletRequest request) {
        log.warn("Handling downstream DeliveryClientException: {}", ex.getMessage());

        ApiErrorDto errorDto = new ApiErrorDto()
            .timestamp(OffsetDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .errorCode("DELIVERY_PROCESSING_ERROR")
            .message(ex.getMessage())
            .path(request.getRequestURI());

        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<ApiErrorDto> handleUnsupported(UnsupportedOperationException ex, HttpServletRequest request) {
        log.warn("Unsupported operation invoked: {}", ex.getMessage());

        ApiErrorDto errorDto = new ApiErrorDto()
            .timestamp(OffsetDateTime.now())
            .status(HttpStatus.NOT_IMPLEMENTED.value())
            .errorCode("NOT_IMPLEMENTED")
            .message(ex.getMessage())
            .path(request.getRequestURI());

        return new ResponseEntity<>(errorDto, HttpStatus.NOT_IMPLEMENTED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDto> handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("Handling unexpected exception in order service", ex);

        ApiErrorDto errorDto = new ApiErrorDto()
            .timestamp(OffsetDateTime.now())
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .errorCode("INTERNAL_SERVER_ERROR")
            .message("An unexpected error occurred in the order service.")
            .path(request.getRequestURI())
            .details(Map.of("originalMessage", ex.getMessage()));

        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}