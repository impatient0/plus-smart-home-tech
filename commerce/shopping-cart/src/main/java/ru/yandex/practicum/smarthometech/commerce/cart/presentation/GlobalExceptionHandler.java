package ru.yandex.practicum.smarthometech.commerce.cart.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.smarthometech.commerce.api.dto.common.ApiErrorDto;
import ru.yandex.practicum.smarthometech.commerce.api.dto.common.ApiErrorDto.HttpStatusEnum;
import ru.yandex.practicum.smarthometech.commerce.api.exception.*;
import ru.yandex.practicum.smarthometech.commerce.api.mapper.ErrorMapper;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ErrorMapper errorMapper;

    @ExceptionHandler(NotAuthorizedUserException.class)
    public ResponseEntity<ApiErrorDto> handleNotAuthorized(NotAuthorizedUserException ex) {
        ApiErrorDto dto = errorMapper.toErrorDto(ex);
        dto.setUserMessage("A valid username is required for this operation.");
        dto.setHttpStatus(ApiErrorDto.HttpStatusEnum._401_UNAUTHORIZED);
        return new ResponseEntity<>(dto, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(NoProductsInShoppingCartException.class)
    public ResponseEntity<ApiErrorDto> handleNoProductsInCart(NoProductsInShoppingCartException ex) {
        ApiErrorDto dto = errorMapper.toErrorDto(ex);
        dto.setUserMessage(String.format("The product with ID '%s' was not found in your cart.", ex.getProductIds().getFirst()));
        dto.setHttpStatus(ApiErrorDto.HttpStatusEnum._400_BAD_REQUEST);
        return new ResponseEntity<>(dto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InsufficientQuantityException.class)
    public ResponseEntity<ApiErrorDto> handleInsufficientQuantity(InsufficientQuantityException ex) {
        ApiErrorDto dto = errorMapper.toErrorDto(ex);
        dto.setUserMessage(String.format("The requested quantity for product '%s' exceeds available stock. Requested: %d, Available: %d.",
            ex.getProductId(), ex.getRequestedQuantity(), ex.getAvailableQuantity()));
        dto.setHttpStatus(ApiErrorDto.HttpStatusEnum._400_BAD_REQUEST);
        return new ResponseEntity<>(dto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ApiErrorDto> handleProductNotFound(ProductNotFoundException ex) {
        ApiErrorDto dto = errorMapper.toErrorDto(ex);
        dto.setUserMessage(String.format("The product with ID '%s' is not currently available.", ex.getProductId()));
        dto.setHttpStatus(ApiErrorDto.HttpStatusEnum._404_NOT_FOUND);
        return new ResponseEntity<>(dto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(WarehouseClientException.class)
    public ResponseEntity<ApiErrorDto> handleWarehouseInteraction(WarehouseClientException ex) {
        ApiErrorDto dto = errorMapper.toErrorDto(ex);
        dto.setUserMessage(ex.getMessage());
        dto.setHttpStatus(HttpStatusEnum._503_SERVICE_UNAVAILABLE);
        return new ResponseEntity<>(dto, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDto> handleGeneric(Exception ex) {
        ApiErrorDto dto = errorMapper.toErrorDto(ex);
        dto.setUserMessage("An unexpected internal error occurred in the shopping cart service.");
        dto.setHttpStatus(ApiErrorDto.HttpStatusEnum._500_INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(dto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}