package ru.yandex.practicum.smarthometech.analyzer.mapper;

import java.util.Set;
import java.util.stream.Collectors;
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
import ru.yandex.practicum.smarthometech.analyzer.domain.entity.ScenarioAction;
import ru.yandex.practicum.smarthometech.analyzer.domain.entity.ScenarioCondition;
import ru.yandex.practicum.smarthometech.analyzer.domain.entity.Sensor;

@Mapper(componentModel = "spring")
public interface EventMapper {

    // --- SCENARIO MAPPING ---

    default Scenario toScenarioEntity(String hubId, ScenarioAddedEventAvro event) {
        if (event == null) {
            return null;
        }

        Scenario scenario = new Scenario();
        scenario.setHubId(hubId);
        scenario.setName(event.getName());

        Set<ScenarioCondition> scenarioConditions = event.getConditions().stream()
            .map(avroCondition -> {
                ScenarioCondition link = new ScenarioCondition();
                link.setScenario(scenario);
                link.setSensor(toSensorEntity(avroCondition.getSensorId()));
                link.setCondition(toConditionEntity(avroCondition));
                return link;
            })
            .collect(Collectors.toSet());
        scenario.setScenarioConditions(scenarioConditions);

        Set<ScenarioAction> scenarioActions = event.getActions().stream()
            .map(avroAction -> {
                ScenarioAction link = new ScenarioAction();
                link.setScenario(scenario);
                link.setSensor(toSensorEntity(avroAction.getSensorId()));
                link.setAction(toActionEntity(avroAction));
                return link;
            })
            .collect(Collectors.toSet());
        scenario.setScenarioActions(scenarioActions);

        return scenario;
    }

    // --- SENSOR MAPPING ---

    @Mapping(target = "id", source = "event.id")
    @Mapping(target = "hubId", source = "hubId")
    Sensor toSensorEntity(String hubId, DeviceAddedEventAvro event);

    // --- HELPER MAPPINGS ---

    @Mapping(target = "id", source = "sensorId")
    @Mapping(target = "hubId", ignore = true)
    Sensor toSensorEntity(String sensorId);

    Condition toConditionEntity(ScenarioConditionAvro avroCondition);

    Action toActionEntity(DeviceActionAvro avroAction);

    String conditionTypeToString(ConditionTypeAvro avroEnum);

    String actionTypeToString(ActionTypeAvro avroEnum);

}
