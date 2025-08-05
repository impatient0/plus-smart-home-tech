package ru.yandex.practicum.smarthometech.commerce.store.presentation.advice;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.smarthometech.commerce.api.dto.common.ApiErrorDto;
import ru.yandex.practicum.smarthometech.commerce.api.mapper.ErrorMapper;
import ru.yandex.practicum.smarthometech.commerce.store.application.exception.ProductNotFoundException;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ErrorMapper errorMapper;

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ApiErrorDto> handleProductNotFoundException(ProductNotFoundException ex) {

        ApiErrorDto errorDto = errorMapper.toErrorDto(ex);

        errorDto.setUserMessage(String.format("The product with ID '%s' could not be found.", ex.getProductId()));
        errorDto.setHttpStatus(ApiErrorDto.HttpStatusEnum._404_NOT_FOUND);

        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDto> handleGenericException(Exception ex) {

        ApiErrorDto errorDto = errorMapper.toErrorDto(ex);

        errorDto.setUserMessage("An unexpected internal error occurred.");
        errorDto.setHttpStatus(ApiErrorDto.HttpStatusEnum._500_INTERNAL_SERVER_ERROR);

        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}