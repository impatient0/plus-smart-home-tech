package ru.yandex.practicum.smarthometech.collector.mapper;

import java.time.Instant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mappings;
import org.mapstruct.ValueMapping;
import ru.yandex.practicum.grpc.telemetry.event.*;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.smarthometech.collector.dto.*;
import ru.yandex.practicum.smarthometech.collector.mapper.qualifier.FullEvent;
import ru.yandex.practicum.smarthometech.collector.mapper.qualifier.Payload;

@Mapper(componentModel = "spring")
public interface EventMapper {

    // --- SENSOR EVENT PROTOBUF -> AVRO MAPPERS ---

    default SensorEventAvro toAvro(SensorEventProto proto) {
        if (proto == null) {
            return null;
        }

        Object avroPayload = switch (proto.getPayloadCase()) {
            case LIGHT_SENSOR_EVENT -> toAvro(proto.getLightSensorEvent());
            case MOTION_SENSOR_EVENT -> toAvro(proto.getMotionSensorEvent());
            case TEMPERATURE_SENSOR_EVENT -> toAvro(proto.getTemperatureSensorEvent());
            case CLIMATE_SENSOR_EVENT -> toAvro(proto.getClimateSensorEvent());
            case SWITCH_SENSOR_EVENT -> toAvro(proto.getSwitchSensorEvent());
            case PAYLOAD_NOT_SET ->
                throw new IllegalArgumentException("SensorEventProto payload is not set.");
        };

        return SensorEventAvro.newBuilder()
            .setId(proto.getId())
            .setHubId(proto.getHubId())
            .setTimestamp(Instant.ofEpochSecond(
                proto.getTimestamp().getSeconds(),
                proto.getTimestamp().getNanos()
            ))
            .setPayload(avroPayload)
            .build();
    }

    // --- Helper methods for Proto -> Avro payload mapping ---

    LightSensorAvro toAvro(LightSensorProto proto);
    MotionSensorAvro toAvro(MotionSensorProto proto);
    TemperatureSensorAvro toAvro(TemperatureSensorProto proto);
    ClimateSensorAvro toAvro(ClimateSensorProto proto);
    SwitchSensorAvro toAvro(SwitchSensorProto proto);

    // --- HUB EVENT PROTOBUF -> AVRO MAPPERS ---

    default HubEventAvro toAvro(HubEventProto proto) {
        if (proto == null) {
            return null;
        }

        Object avroPayload = switch (proto.getPayloadCase()) {
            case DEVICE_ADDED -> toAvro(proto.getDeviceAdded());
            case DEVICE_REMOVED -> toAvro(proto.getDeviceRemoved());
            case SCENARIO_ADDED -> toAvro(proto.getScenarioAdded());
            case SCENARIO_REMOVED -> toAvro(proto.getScenarioRemoved());
            case PAYLOAD_NOT_SET ->
                throw new IllegalArgumentException("HubEventProto payload is not set.");
        };

        if (avroPayload == null) {
            return null;
        }

        return HubEventAvro.newBuilder()
            .setHubId(proto.getHubId())
            .setTimestamp(Instant.ofEpochSecond(
                proto.getTimestamp().getSeconds(),
                proto.getTimestamp().getNanos()
            ))
            .setPayload(avroPayload)
            .build();
    }

    // --- Helper methods for Proto -> Avro payload mapping ---

    DeviceAddedEventAvro toAvro(DeviceAddedEventProto proto);
    DeviceRemovedEventAvro toAvro(DeviceRemovedEventProto proto);
    @Mappings({
        @Mapping(source = "conditionsList", target = "conditions"),
        @Mapping(source = "actionsList", target = "actions")
    })
    ScenarioAddedEventAvro toAvro(ScenarioAddedEventProto proto);
    ScenarioRemovedEventAvro toAvro(ScenarioRemovedEventProto proto);

    default ScenarioConditionAvro toAvro(ScenarioConditionProto proto) {
        if (proto == null) {
            return null;
        }

        Object value = switch (proto.getValueCase()) {
            case BOOL_VALUE -> proto.getBoolValue();
            case INT_VALUE -> proto.getIntValue();
            case VALUE_NOT_SET -> null;
        };

        return ScenarioConditionAvro.newBuilder()
            .setSensorId(proto.getSensorId())
            .setType(toAvro(proto.getType()))
            .setOperation(toAvro(proto.getOperation()))
            .setValue(value)
            .build();
    }

    @ValueMapping(source = "UNRECOGNIZED", target = MappingConstants.THROW_EXCEPTION)
    ConditionTypeAvro toAvro(ConditionTypeProto proto);
    @ValueMapping(source = "UNRECOGNIZED", target = MappingConstants.THROW_EXCEPTION)
    ConditionOperationAvro toAvro(ConditionOperationProto proto);
    @ValueMapping(source = "UNRECOGNIZED", target = MappingConstants.THROW_EXCEPTION)
    DeviceTypeAvro toAvro(DeviceTypeProto proto);
    @ValueMapping(source = "UNRECOGNIZED", target = MappingConstants.THROW_EXCEPTION)
    ActionTypeAvro toAvro(ActionTypeProto proto);


    // --- SENSOR EVENT AVRO <-> DTO MAPPERS ---

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

    // --- HUB EVENT AVRO <-> DTO MAPPERS ---

    @Mapping(target = "payload", source = "dto", qualifiedBy = Payload.class)
    @FullEvent
    HubEventAvro toAvro(DeviceAddedEvent dto);
    @Payload
    @Mapping(target = "type", source = "deviceType")
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
