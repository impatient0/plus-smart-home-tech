package ru.yandex.practicum.smarthometech.commerce.store.presentation;

import jakarta.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.smarthometech.commerce.api.dto.common.ApiErrorDto;
import ru.yandex.practicum.smarthometech.commerce.api.exception.ProductNotFoundException;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ApiErrorDto> handleProductNotFoundException(ProductNotFoundException ex, HttpServletRequest request) {

        ApiErrorDto errorDto = new ApiErrorDto()
            .timestamp(OffsetDateTime.now())
            .status(HttpStatus.NOT_FOUND.value())
            .errorCode("PRODUCT_NOT_FOUND_IN_STORE")
            .message(String.format("The product with ID '%s' could not be found.", ex.getProductId()))
            .path(request.getRequestURI())
            .details(Map.of("productId", ex.getProductId().toString()));

        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorDto> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {

        ApiErrorDto errorDto = new ApiErrorDto()
            .timestamp(OffsetDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .errorCode("INVALID_ARGUMENT")
            .message(ex.getMessage())
            .path(request.getRequestURI());

        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDto> handleGenericException(Exception ex, HttpServletRequest request) {

        ApiErrorDto errorDto = new ApiErrorDto()
            .timestamp(OffsetDateTime.now())
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .errorCode("INTERNAL_SERVER_ERROR")
            .message("An unexpected error occurred.")
            .path(request.getRequestURI());

        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}