package ru.yandex.practicum.smarthometech.commerce.api.utility;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.smarthometech.commerce.api.dto.common.ApiErrorDto;


@Slf4j
public class ErrorParser {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Optional<ApiErrorDto> parseErrorDto(Response response) {
        if (response.body() == null) {
            return Optional.empty();
        }
        try (InputStream bodyIs = response.body().asInputStream()) {
            return Optional.of(objectMapper.readValue(bodyIs, ApiErrorDto.class));
        } catch (IOException e) {
            log.error("Failed to decode error response body from Warehouse service", e);
            return Optional.empty();
        }
    }

    public static Optional<UUID> extractUuid(ApiErrorDto dto, String key) {
        if (dto == null || dto.getDetails() == null) return Optional.empty();
        return Optional.ofNullable(dto.getDetails().get(key))
            .filter(String.class::isInstance)
            .map(String.class::cast)
            .map(UUID::fromString);
    }

    public static Optional<Long> extractLong(ApiErrorDto dto, String key) {
        if (dto == null || dto.getDetails() == null) return Optional.empty();
        return Optional.ofNullable(dto.getDetails().get(key))
            .filter(Number.class::isInstance)
            .map(Number.class::cast)
            .map(Number::longValue);
    }
}
