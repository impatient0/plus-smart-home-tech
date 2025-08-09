package ru.yandex.practicum.smarthometech.commerce.cart.presentation;

import jakarta.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.smarthometech.commerce.api.dto.common.ApiErrorDto;
import ru.yandex.practicum.smarthometech.commerce.api.exception.*;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @ExceptionHandler(NotAuthorizedUserException.class)
    public ResponseEntity<ApiErrorDto> handleNotAuthorized(NotAuthorizedUserException ex, HttpServletRequest request) {

        ApiErrorDto errorDto = new ApiErrorDto()
            .timestamp(OffsetDateTime.now())
            .status(HttpStatus.UNAUTHORIZED.value())
            .errorCode("USER_NOT_AUTHORIZED")
            .message("A valid username is required for this operation.")
            .path(request.getRequestURI());

        return new ResponseEntity<>(errorDto, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(NoProductsInShoppingCartException.class)
    public ResponseEntity<ApiErrorDto> handleNoProductsInCart(NoProductsInShoppingCartException ex, HttpServletRequest request) {

        ApiErrorDto errorDto = new ApiErrorDto()
            .timestamp(OffsetDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .errorCode("PRODUCT_NOT_IN_CART")
            .message(String.format("The product with ID '%s' was not found in your cart.", ex.getProductIds().getFirst()))
            .path(request.getRequestURI())
            .details(Map.of("productIds", ex.getProductIds()));

        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InsufficientQuantityException.class)
    public ResponseEntity<ApiErrorDto> handleInsufficientQuantity(InsufficientQuantityException ex, HttpServletRequest request) {

        ApiErrorDto errorDto = new ApiErrorDto()
            .timestamp(OffsetDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .errorCode("INSUFFICIENT_QUANTITY")
            .message(String.format("The requested quantity for product '%s' exceeds available stock. Requested: %d, Available: %d.",
                ex.getProductId(), ex.getRequestedQuantity(), ex.getAvailableQuantity()))
            .path(request.getRequestURI())
            .details(Map.of(
                "productId", ex.getProductId().toString(),
                "availableQuantity", ex.getAvailableQuantity(),
                "requestedQuantity", ex.getRequestedQuantity()
            ));

        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ApiErrorDto> handleProductNotFound(ProductNotFoundException ex, HttpServletRequest request) {

        ApiErrorDto errorDto = new ApiErrorDto()
            .timestamp(OffsetDateTime.now())
            .status(HttpStatus.NOT_FOUND.value())
            .errorCode("PRODUCT_NOT_FOUND")
            .message(String.format("The product with ID '%s' is not currently available.", ex.getProductId()))
            .path(request.getRequestURI())
            .details(Map.of("productId", ex.getProductId().toString()));

        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(WarehouseClientException.class)
    public ResponseEntity<ApiErrorDto> handleWarehouseInteraction(WarehouseClientException ex, HttpServletRequest request) {

        ApiErrorDto errorDto = new ApiErrorDto()
            .timestamp(OffsetDateTime.now())
            .status(HttpStatus.SERVICE_UNAVAILABLE.value())
            .errorCode("WAREHOUSE_SERVICE_UNAVAILABLE")
            .message("Warehouse service is currently unavailable.")
            .path(request.getRequestURI())
            .details(Map.of("originalMessage", ex.getMessage()));

        return new ResponseEntity<>(errorDto, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDto> handleGeneric(Exception ex, HttpServletRequest request) {

        ApiErrorDto errorDto = new ApiErrorDto()
            .timestamp(OffsetDateTime.now())
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .errorCode("INTERNAL_SERVER_ERROR")
            .message("An unexpected error occurred.")
            .path(request.getRequestURI())
            .details(Map.of("originalMessage", ex.getMessage()));

        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}