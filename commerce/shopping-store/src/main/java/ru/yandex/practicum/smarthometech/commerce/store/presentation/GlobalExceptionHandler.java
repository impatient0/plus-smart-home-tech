package ru.yandex.practicum.smarthometech.commerce.store.presentation;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.smarthometech.commerce.api.dto.common.ApiErrorDto;
import ru.yandex.practicum.smarthometech.commerce.api.mapper.ErrorMapper;
import ru.yandex.practicum.smarthometech.commerce.api.exception.ProductNotFoundException;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ErrorMapper errorMapper;

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ApiErrorDto> handleProductNotFoundException(ProductNotFoundException ex) {

        ApiErrorDto errorDto = errorMapper.toErrorDto(ex);

        errorDto.setUserMessage(String.format("The product with ID '%s' could not be found.", ex.getProductId()));
        errorDto.setHttpStatus(ApiErrorDto.HttpStatusEnum._404_NOT_FOUND);
        errorDto.setDetails(Map.of("productId", ex.getProductId().toString()));

        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorDto> handleIllegalArgumentException(IllegalArgumentException ex) {
        ApiErrorDto errorDto = errorMapper.toErrorDto(ex);

        errorDto.setUserMessage(ex.getMessage());
        errorDto.setHttpStatus(ApiErrorDto.HttpStatusEnum._400_BAD_REQUEST);

        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDto> handleGenericException(Exception ex) {

        ApiErrorDto errorDto = errorMapper.toErrorDto(ex);

        errorDto.setUserMessage("An unexpected internal error occurred.");
        errorDto.setHttpStatus(ApiErrorDto.HttpStatusEnum._500_INTERNAL_SERVER_ERROR);

        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}