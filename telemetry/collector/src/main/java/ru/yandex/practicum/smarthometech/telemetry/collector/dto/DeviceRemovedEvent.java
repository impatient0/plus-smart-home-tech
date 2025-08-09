package ru.yandex.practicum.smarthometech.telemetry.collector.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DeviceRemovedEvent extends HubEvent {

    @NotBlank(message = "ID cannot be blank")
    private String id;
}
