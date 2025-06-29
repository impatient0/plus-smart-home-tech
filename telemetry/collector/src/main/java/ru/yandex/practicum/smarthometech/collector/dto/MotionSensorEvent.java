package ru.yandex.practicum.smarthometech.collector.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MotionSensorEvent extends SensorEvent {

    @Min(value = 0, message = "Link quality cannot be negative")
    private int linkQuality;

    private boolean motion;

    @Min(value = 0, message = "Voltage cannot be negative")
    private int voltage;
}
