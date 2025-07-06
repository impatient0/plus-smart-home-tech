package ru.yandex.practicum.smarthometech.collector.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DeviceAddedEvent extends HubEvent {

    @NotNull(message = "Must have an ID")
    private String id;

    @NotBlank(message = "Type cannot be blank")
    private String deviceType;
}
