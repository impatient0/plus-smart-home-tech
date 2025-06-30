package ru.yandex.practicum.smarthometech.collector.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;
import ru.yandex.practicum.smarthometech.collector.dto.ClimateSensorEvent;
import ru.yandex.practicum.smarthometech.collector.dto.DeviceAddedEvent;
import ru.yandex.practicum.smarthometech.collector.dto.DeviceRemovedEvent;
import ru.yandex.practicum.smarthometech.collector.dto.LightSensorEvent;
import ru.yandex.practicum.smarthometech.collector.dto.MotionSensorEvent;
import ru.yandex.practicum.smarthometech.collector.dto.ScenarioAddedEvent;
import ru.yandex.practicum.smarthometech.collector.dto.ScenarioRemovedEvent;
import ru.yandex.practicum.smarthometech.collector.dto.SwitchSensorEvent;
import ru.yandex.practicum.smarthometech.collector.dto.TemperatureSensorEvent;
import ru.yandex.practicum.smarthometech.collector.mapper.qualifier.FullEvent;
import ru.yandex.practicum.smarthometech.collector.mapper.qualifier.Payload;

@Mapper(componentModel = "spring")
public interface EventMapper {

    // --- SENSOR EVENT MAPPERS ---

    @Mapping(target = "payload", source = "dto", qualifiedBy = Payload.class)
    @FullEvent
    SensorEventAvro toAvro(LightSensorEvent dto);
    @Payload
    LightSensorAvro toPayload(LightSensorEvent dto);

    @Mapping(target = "payload", source = "dto", qualifiedBy = Payload.class)
    @FullEvent
    SensorEventAvro toAvro(MotionSensorEvent dto);
    @Payload
    MotionSensorAvro toPayload(MotionSensorEvent dto);

    @Mapping(target = "payload", source = "dto", qualifiedBy = Payload.class)
    @FullEvent
    SensorEventAvro toAvro(ClimateSensorEvent dto);
    @Payload
    ClimateSensorAvro toPayload(ClimateSensorEvent dto);

    @Mapping(target = "payload", source = "dto", qualifiedBy = Payload.class)
    @FullEvent
    SensorEventAvro toAvro(SwitchSensorEvent dto);
    @Payload
    SwitchSensorAvro toPayload(SwitchSensorEvent dto);

    @Mapping(target = "payload", source = "dto", qualifiedBy = Payload.class)
    @FullEvent
    SensorEventAvro toAvro(TemperatureSensorEvent dto);
    @Payload
    TemperatureSensorAvro toPayload(TemperatureSensorEvent dto);

    // --- HUB EVENT MAPPERS ---

    @Mapping(target = "payload", source = "dto", qualifiedBy = Payload.class)
    @FullEvent
    HubEventAvro toAvro(DeviceAddedEvent dto);
    @Payload
    DeviceAddedEventAvro toPayload(DeviceAddedEvent dto);

    @Mapping(target = "payload", source = "dto", qualifiedBy = Payload.class)
    @FullEvent
    HubEventAvro toAvro(DeviceRemovedEvent dto);
    @Payload
    DeviceRemovedEvent toPayload(DeviceRemovedEvent dto);

    @Mapping(target = "payload", source = "dto", qualifiedBy = Payload.class)
    @FullEvent
    HubEventAvro toAvro(ScenarioAddedEvent dto);
    @Payload
    ScenarioAddedEventAvro toPayload(ScenarioAddedEvent dto);

    @Mapping(target = "payload", source = "dto", qualifiedBy = Payload.class)
    @FullEvent
    HubEventAvro toAvro(ScenarioRemovedEvent dto);
    @Payload
    ScenarioRemovedEventAvro toPayload(ScenarioRemovedEvent dto);

}
