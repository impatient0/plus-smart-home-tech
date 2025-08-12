package ru.yandex.practicum.smarthometech.telemetry.collector.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class ScenarioCondition {

    @NotBlank(message = "Sensor ID cannot be blank")
    private String sensorId;

    @NotNull(message = "Must have a condition type")
    private ConditionType type;

    @NotNull(message = "Must have a condition operation")
    private ConditionOperation operation;

    private Integer value;
}
