package ru.yandex.practicum.smarthometech.commerce.warehouse.presentation.advice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.smarthometech.commerce.api.dto.common.ApiErrorDto;
import ru.yandex.practicum.smarthometech.commerce.api.mapper.ErrorMapper;
import ru.yandex.practicum.smarthometech.commerce.warehouse.application.exception.InsufficientQuantityException;
import ru.yandex.practicum.smarthometech.commerce.warehouse.application.exception.ProductAlreadyExistsException;
import ru.yandex.practicum.smarthometech.commerce.warehouse.application.exception.ProductNotFoundException;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private final ErrorMapper errorMapper;

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ApiErrorDto> handleProductNotFoundException(ProductNotFoundException ex) {
        log.warn("Handling ProductNotFoundException: {}", ex.getMessage());

        ApiErrorDto errorDto = errorMapper.toErrorDto(ex);
        errorDto.setUserMessage(String.format("Product with ID '%s' is not registered in the warehouse.", ex.getProductId()));
        errorDto.setHttpStatus(ApiErrorDto.HttpStatusEnum._404_NOT_FOUND);

        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ProductAlreadyExistsException.class)
    public ResponseEntity<ApiErrorDto> handleProductAlreadyExistsException(ProductAlreadyExistsException ex) {
        log.warn("Handling ProductAlreadyExistsException: {}", ex.getMessage());

        ApiErrorDto errorDto = errorMapper.toErrorDto(ex);
        errorDto.setUserMessage(String.format("Product with ID '%s' is already registered in the warehouse.", ex.getProductId()));
        errorDto.setHttpStatus(ApiErrorDto.HttpStatusEnum._400_BAD_REQUEST);

        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InsufficientQuantityException.class)
    public ResponseEntity<ApiErrorDto> handleInsufficientQuantityException(InsufficientQuantityException ex) {
        log.warn("Handling InsufficientQuantityException: {}", ex.getMessage());

        ApiErrorDto errorDto = errorMapper.toErrorDto(ex);
        errorDto.setUserMessage(String.format(
            "Insufficient stock for product ID '%s'. Requested: %d, Available: %d.",
            ex.getProductId(), ex.getRequestedQuantity(), ex.getAvailableQuantity()
        ));
        errorDto.setHttpStatus(ApiErrorDto.HttpStatusEnum._400_BAD_REQUEST);

        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDto> handleGenericException(Exception ex) {
        log.error("Handling unexpected exception", ex);

        ApiErrorDto errorDto = errorMapper.toErrorDto(ex);
        errorDto.setUserMessage("An unexpected internal server error occurred.");
        errorDto.setHttpStatus(ApiErrorDto.HttpStatusEnum._500_INTERNAL_SERVER_ERROR);

        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}