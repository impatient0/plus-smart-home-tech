package ru.yandex.practicum.smarthometech.collector.mapper;

import java.time.Instant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import ru.yandex.practicum.grpc.telemetry.event.*;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.smarthometech.collector.dto.*;
import ru.yandex.practicum.smarthometech.collector.mapper.qualifier.FullEvent;
import ru.yandex.practicum.smarthometech.collector.mapper.qualifier.Payload;

@Mapper(componentModel = "spring")
public interface EventMapper {

    // --- SENSOR EVENT PROTOBUF -> AVRO MAPPERS

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

    default ConditionTypeAvro toAvro(ConditionTypeProto proto) {
        if (proto == null) {
            return null;
        }

        return switch (proto) {
            case MOTION -> ConditionTypeAvro.MOTION;
            case LUMINOSITY -> ConditionTypeAvro.LUMINOSITY;
            case SWITCH -> ConditionTypeAvro.SWITCH;
            case TEMPERATURE -> ConditionTypeAvro.TEMPERATURE;
            case CO2LEVEL -> ConditionTypeAvro.CO2LEVEL;
            case HUMIDITY -> ConditionTypeAvro.HUMIDITY;

            case CONDITION_TYPE_UNKNOWN, UNRECOGNIZED -> ConditionTypeAvro.MOTION;
        };
    }

    default ConditionOperationAvro toAvro(ConditionOperationProto proto) {
        if (proto == null) {
            return null;
        }

        return switch (proto) {
            case EQUALS -> ConditionOperationAvro.EQUALS;
            case GREATER_THAN -> ConditionOperationAvro.GREATER_THAN;
            case LOWER_THAN -> ConditionOperationAvro.LOWER_THAN;

            case CONDITION_OPERATION_UNKNOWN, UNRECOGNIZED -> ConditionOperationAvro.EQUALS;
        };
    }

    default ActionTypeAvro toAvro(ActionTypeProto proto) {
        if (proto == null) {
            return null;
        }

        return switch (proto) {
            case ACTIVATE -> ActionTypeAvro.ACTIVATE;
            case DEACTIVATE -> ActionTypeAvro.DEACTIVATE;
            case INVERSE -> ActionTypeAvro.INVERSE;
            case SET_VALUE -> ActionTypeAvro.SET_VALUE;

            case ACTION_TYPE_UNKNOWN, UNRECOGNIZED -> ActionTypeAvro.ACTIVATE;
        };
    }

    default DeviceTypeAvro toAvro(DeviceTypeProto proto) {
        if (proto == null) {
            return null;
        }

        return switch (proto) {
            case MOTION_SENSOR -> DeviceTypeAvro.MOTION_SENSOR;
            case TEMPERATURE_SENSOR -> DeviceTypeAvro.TEMPERATURE_SENSOR;
            case LIGHT_SENSOR -> DeviceTypeAvro.LIGHT_SENSOR;
            case CLIMATE_SENSOR -> DeviceTypeAvro.CLIMATE_SENSOR;
            case SWITCH_SENSOR -> DeviceTypeAvro.SWITCH_SENSOR;

            case DEVICE_TYPE_UNKNOWN, UNRECOGNIZED -> DeviceTypeAvro.MOTION_SENSOR;
        };
    }

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
