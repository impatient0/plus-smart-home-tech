package ru.yandex.practicum.smarthometech.collector.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ClimateSensorEvent extends SensorEvent {

    @Min(value = -40, message = "Temperature cannot be below -40 C")
    @Max(value = 50, message = "Temperature cannot be above 50 C")
    private int temperatureC;

    @Min(value = 0, message = "Humidity cannot be negative")
    @Max(value = 100, message = "Humidity cannot be over 100%")
    private int humidity;

    @Min(value = 0, message = "CO2 level cannot be negative")
    private int co2Level;
}
