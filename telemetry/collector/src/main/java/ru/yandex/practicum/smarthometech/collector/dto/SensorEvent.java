package ru.yandex.practicum.smarthometech.collector.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type",
    visible = true
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = LightSensorEvent.class, name = "LIGHT_SENSOR"),
    @JsonSubTypes.Type(value = MotionSensorEvent.class, name = "MOTION_SENSOR"),
    @JsonSubTypes.Type(value = TemperatureSensorEvent.class, name = "TEMPERATURE_SENSOR"),
    @JsonSubTypes.Type(value = ClimateSensorEvent.class, name = "CLIMATE_SENSOR"),
    @JsonSubTypes.Type(value = SwitchSensorEvent.class, name = "SWITCH_SENSOR")
})
@Data
public abstract class SensorEvent {
    @NotBlank
    private String id;

    @NotBlank
    private String hubId;

    private Instant timestamp = Instant.now();

    @NotNull
    private SensorEventType type;
}