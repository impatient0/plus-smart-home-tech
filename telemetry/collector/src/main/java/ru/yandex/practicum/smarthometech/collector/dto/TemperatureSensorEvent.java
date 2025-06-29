package ru.yandex.practicum.smarthometech.collector.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TemperatureSensorEvent extends SensorEvent {

    @Min(value = -40, message = "Temperature cannot be below -40 C")
    @Max(value = 50, message = "Temperature cannot be above 50 C")
    private int temperature_c;

    @Min(value = -40, message = "Temperature cannot be below -40 F")
    @Max(value = 120, message = "Temperature cannot be above 120 F")
    private int temperature_f;
}
