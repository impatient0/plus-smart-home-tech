package ru.yandex.practicum.smarthometech.commerce.api.mapper;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import ru.yandex.practicum.smarthometech.commerce.api.dto.common.ApiErrorDto;
import ru.yandex.practicum.smarthometech.commerce.api.dto.common.ApiErrorDtoCause;
import ru.yandex.practicum.smarthometech.commerce.api.dto.common.ApiErrorDtoCauseStackTraceInner;

@Mapper(componentModel = "spring")
public interface ErrorMapper {

    default ApiErrorDto toErrorDto(Throwable throwable) {
        if (throwable == null) {
            return null;
        }

        ApiErrorDto dto = new ApiErrorDto();
        dto.setMessage(throwable.getMessage());
        dto.setLocalizedMessage(throwable.getLocalizedMessage());

        dto.setCause(toCauseDto(throwable.getCause()));

        if (throwable.getStackTrace() != null) {
            List<ApiErrorDtoCauseStackTraceInner> stackTrace = Arrays.stream(throwable.getStackTrace())
                .map(this::toStackTraceInnerDto)
                .collect(Collectors.toList());
            dto.setStackTrace(stackTrace);
        }

        if (throwable.getSuppressed() != null) {
            dto.setSuppressed(toSuppressedDto(throwable.getSuppressed()));
        }

        return dto;
    }

    ApiErrorDtoCause toCauseDto(Throwable cause);

    List<ApiErrorDtoCause> toSuppressedDto(Throwable[] suppressed);

    ApiErrorDtoCauseStackTraceInner toStackTraceInnerDto(StackTraceElement element);
}