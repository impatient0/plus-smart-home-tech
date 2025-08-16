package ru.yandex.practicum.smarthometech.commerce.warehouse.presentation;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.smarthometech.commerce.api.dto.common.ApiErrorDto;
import ru.yandex.practicum.smarthometech.commerce.api.exception.InsufficientQuantityException;
import ru.yandex.practicum.smarthometech.commerce.api.exception.ProductAlreadyExistsException;
import ru.yandex.practicum.smarthometech.commerce.api.exception.ProductNotFoundException;

import java.time.OffsetDateTime;
import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ApiErrorDto> handleProductNotFoundException(ProductNotFoundException ex, HttpServletRequest request) {
        log.warn("Handling ProductNotFoundException for product ID '{}': {}", ex.getProductId(), ex.getMessage());

        ApiErrorDto errorDto = new ApiErrorDto()
            .timestamp(OffsetDateTime.now())
            .status(HttpStatus.NOT_FOUND.value())
            .errorCode("PRODUCT_NOT_FOUND_IN_WAREHOUSE")
            .message(String.format("Product with ID '%s' is not registered in the warehouse.", ex.getProductId()))
            .path(request.getRequestURI())
            .details(Map.of("productId", ex.getProductId().toString()));

        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ProductAlreadyExistsException.class)
    public ResponseEntity<ApiErrorDto> handleProductAlreadyExistsException(ProductAlreadyExistsException ex, HttpServletRequest request) {
        log.warn("Handling ProductAlreadyExistsException for product ID '{}': {}", ex.getProductId(), ex.getMessage());

        ApiErrorDto errorDto = new ApiErrorDto()
            .timestamp(OffsetDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .errorCode("PRODUCT_ALREADY_EXISTS_IN_WAREHOUSE")
            .message(String.format("Product with ID '%s' already exists.", ex.getProductId()))
            .path(request.getRequestURI())
            .details(Map.of("productId", ex.getProductId().toString()));

        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InsufficientQuantityException.class)
    public ResponseEntity<ApiErrorDto> handleInsufficientQuantityException(InsufficientQuantityException ex, HttpServletRequest request) {
        log.warn("Handling InsufficientQuantityException for product '{}': Requested {}, available {}",
            ex.getProductId(), ex.getRequestedQuantity(), ex.getAvailableQuantity());

        ApiErrorDto errorDto = new ApiErrorDto()
            .timestamp(OffsetDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .errorCode("INSUFFICIENT_QUANTITY_IN_WAREHOUSE")
            .message(String.format("Insufficient stock for product ID '%s'.", ex.getProductId()))
            .path(request.getRequestURI())
            .details(Map.of(
                "productId", ex.getProductId().toString(),
                "requestedQuantity", ex.getRequestedQuantity(),
                "availableQuantity", ex.getAvailableQuantity()
            ));

        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDto> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Handling unexpected exception in warehouse service", ex);

        ApiErrorDto errorDto = new ApiErrorDto()
            .timestamp(OffsetDateTime.now())
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .errorCode("INTERNAL_SERVER_ERROR")
            .message("An unexpected internal error occurred.")
            .path(request.getRequestURI())
            .details(Map.of("originalMessage", ex.getMessage()));

        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}