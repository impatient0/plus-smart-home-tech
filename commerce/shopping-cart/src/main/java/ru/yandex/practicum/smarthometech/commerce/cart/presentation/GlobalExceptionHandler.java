package ru.yandex.practicum.smarthometech.commerce.cart.presentation;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.smarthometech.commerce.api.dto.common.ApiErrorDto;
import ru.yandex.practicum.smarthometech.commerce.api.exception.*;

import java.time.OffsetDateTime;
import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(NotAuthorizedUserException.class)
    public ResponseEntity<ApiErrorDto> handleNotAuthorized(NotAuthorizedUserException ex, HttpServletRequest request) {
        log.warn("Handling NotAuthorizedUserException: {}", ex.getMessage());

        ApiErrorDto errorDto = new ApiErrorDto()
            .timestamp(OffsetDateTime.now())
            .status(HttpStatus.UNAUTHORIZED.value())
            .errorCode("USER_NOT_AUTHORIZED")
            .message("User is not authorized to perform this action.")
            .path(request.getRequestURI());

        return new ResponseEntity<>(errorDto, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(NoProductsInShoppingCartException.class)
    public ResponseEntity<ApiErrorDto> handleNoProductsInCart(NoProductsInShoppingCartException ex, HttpServletRequest request) {
        log.warn("Handling NoProductsInShoppingCartException for products: {}", ex.getProductIds());

        ApiErrorDto errorDto = new ApiErrorDto()
            .timestamp(OffsetDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .errorCode("PRODUCT_NOT_IN_CART")
            .message("One or more of the specified products were not found in the cart.")
            .path(request.getRequestURI())
            .details(Map.of("productIds", ex.getProductIds()));

        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InsufficientQuantityException.class)
    public ResponseEntity<ApiErrorDto> handleInsufficientQuantity(InsufficientQuantityException ex, HttpServletRequest request) {
        log.warn("Handling InsufficientQuantityException for product '{}': Requested {}, but only {} available.",
            ex.getProductId(), ex.getRequestedQuantity(), ex.getAvailableQuantity());

        ApiErrorDto errorDto = new ApiErrorDto()
            .timestamp(OffsetDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .errorCode("INSUFFICIENT_QUANTITY")
            .message(String.format("The requested quantity for product '%s' exceeds available stock.", ex.getProductId()))
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
        log.warn("Handling ProductNotFoundException for product ID '{}': {}", ex.getProductId(), ex.getMessage());

        ApiErrorDto errorDto = new ApiErrorDto()
            .timestamp(OffsetDateTime.now())
            .status(HttpStatus.NOT_FOUND.value())
            .errorCode("PRODUCT_NOT_FOUND")
            .message(String.format("The product with ID '%s' could not be found.", ex.getProductId()))
            .path(request.getRequestURI())
            .details(Map.of("productId", ex.getProductId().toString()));

        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(WarehouseClientException.class)
    public ResponseEntity<ApiErrorDto> handleWarehouseInteraction(WarehouseClientException ex, HttpServletRequest request) {
        log.error("Handling WarehouseClientException while communicating with the warehouse service", ex);

        ApiErrorDto errorDto = new ApiErrorDto()
            .timestamp(OffsetDateTime.now())
            .status(HttpStatus.SERVICE_UNAVAILABLE.value())
            .errorCode("WAREHOUSE_SERVICE_UNAVAILABLE")
            .message("Could not complete the operation due to an issue with an external service.")
            .path(request.getRequestURI())
            .details(Map.of("originalMessage", ex.getMessage()));

        return new ResponseEntity<>(errorDto, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDto> handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("Handling unexpected exception in cart service", ex);

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