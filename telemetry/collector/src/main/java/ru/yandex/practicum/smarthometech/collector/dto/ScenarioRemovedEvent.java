package ru.yandex.practicum.smarthometech.collector.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ScenarioRemovedEvent extends HubEvent {

    @NotBlank(message = "Scenario name cannot be blank")
    @Size(min = 3, message = "Scenario name must be at least 3 characters long")
    private String name;
}
