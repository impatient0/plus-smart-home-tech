package ru.yandex.practicum.smarthometech.telemetry.collector.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class DeviceAction {

    @NotBlank(message = "Sensor ID cannot be blank")
    private String sensorId;

    @NotNull(message = "Must have an action type")
    private ActionType type;

    private Integer value;
}
