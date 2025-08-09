package ru.yandex.practicum.smarthometech.telemetry.collector.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import org.springframework.validation.annotation.Validated;

@Data
@EqualsAndHashCode(callSuper = true)
@Validated
public class ScenarioAddedEvent extends HubEvent {

    @NotBlank(message = "Scenario name cannot be blank")
    @Size(min = 3, message = "Scenario name must be at least 3 characters long")
    private String name;

    private List<ScenarioCondition> conditions;

    private List<DeviceAction> actions;
}