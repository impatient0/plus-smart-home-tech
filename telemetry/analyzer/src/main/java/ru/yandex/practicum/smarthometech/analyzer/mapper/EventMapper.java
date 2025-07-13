package ru.yandex.practicum.smarthometech.analyzer.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.smarthometech.analyzer.domain.entity.Action;
import ru.yandex.practicum.smarthometech.analyzer.domain.entity.Condition;
import ru.yandex.practicum.smarthometech.analyzer.domain.entity.Scenario;
import ru.yandex.practicum.smarthometech.analyzer.domain.entity.Sensor;

@Mapper(componentModel = "spring")
public interface EventMapper {

    // --- AVRO EVENT -> ENTITY MAPPERS ---

    @Mapping(target = "id", source = "event.id")
    @Mapping(target = "hubId", source = "hubId")
    Sensor toSensorEntity(String hubId, DeviceAddedEventAvro event);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "hubId", source = "hubId")
    @Mapping(target = "name", source = "event.name")
    @Mapping(target = "conditions", source = "event.conditions")
    @Mapping(target = "actions", source = "event.actions")
    Scenario toScenarioEntity(String hubId, ScenarioAddedEventAvro event);

    Condition toConditionEntity(ScenarioConditionAvro avroCondition);

    Action toActionEntity(DeviceActionAvro avroAction);

    String conditionTypeToString(ConditionTypeAvro avroEnum);

    String actionTypeToString(ActionTypeAvro avroEnum);

}
