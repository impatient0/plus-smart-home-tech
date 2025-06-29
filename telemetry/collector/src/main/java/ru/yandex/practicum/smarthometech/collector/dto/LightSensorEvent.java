package ru.yandex.practicum.smarthometech.collector.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class LightSensorEvent extends SensorEvent {

    @Min(value = 0, message = "Link quality cannot be negative")
    private int linkQuality;

    @Min(value = 0, message = "Luminosity cannot be negative")
    private int luminosity;
}