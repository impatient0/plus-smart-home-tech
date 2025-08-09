package ru.yandex.practicum.smarthometech.telemetry.analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.smarthometech.telemetry.analyzer.domain.entity.*;
import ru.yandex.practicum.smarthometech.telemetry.analyzer.mapper.EventMapper;
import ru.yandex.practicum.smarthometech.telemetry.analyzer.repository.*;

import java.util.HashSet;
import java.util.Set;
import ru.yandex.practicum.smarthometech.telemetry.analyzer.repository.ActionRepository;
import ru.yandex.practicum.smarthometech.telemetry.analyzer.repository.ConditionRepository;
import ru.yandex.practicum.smarthometech.telemetry.analyzer.repository.ScenarioRepository;
import ru.yandex.practicum.smarthometech.telemetry.analyzer.repository.SensorRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScenarioManagementService {

    private final ScenarioRepository scenarioRepository;
    private final ConditionRepository conditionRepository;
    private final ActionRepository actionRepository;
    private final SensorRepository sensorRepository;
    private final EventMapper eventMapper;

    @Transactional
    public void saveOrUpdateScenario(HubEventAvro hubEvent) {
        log.info("Saving or updating scenario for hubId: {}", hubEvent.getHubId());

        if (!(hubEvent.getPayload() instanceof ScenarioAddedEventAvro event)) {
            return;
        }

        Scenario transientScenario = eventMapper.toScenarioEntity(hubEvent.getHubId(), event);

        Scenario managedScenario = scenarioRepository
            .findByHubIdAndName(transientScenario.getHubId(), transientScenario.getName())
            .orElse(transientScenario);

        Set<ScenarioCondition> managedConditions = new HashSet<>();
        for (ScenarioCondition transientLink : transientScenario.getScenarioConditions()) {
            Sensor sensor = findOrCreateSensor(transientLink.getSensor());
            Condition condition = findOrCreateCondition(transientLink.getCondition());

            ScenarioCondition managedLink = new ScenarioCondition();
            managedLink.setScenario(managedScenario);
            managedLink.setSensor(sensor);
            managedLink.setCondition(condition);
            managedConditions.add(managedLink);
        }

        Set<ScenarioAction> managedActions = new HashSet<>();
        for (ScenarioAction transientLink : transientScenario.getScenarioActions()) {
            Sensor sensor = findOrCreateSensor(transientLink.getSensor());
            Action action = findOrCreateAction(transientLink.getAction());

            ScenarioAction managedLink = new ScenarioAction();
            managedLink.setScenario(managedScenario);
            managedLink.setSensor(sensor);
            managedLink.setAction(action);
            managedActions.add(managedLink);
        }

        managedScenario.getScenarioConditions().clear();
        managedScenario.getScenarioConditions().addAll(managedConditions);

        managedScenario.getScenarioActions().clear();
        managedScenario.getScenarioActions().addAll(managedActions);

        scenarioRepository.save(managedScenario);
    }

    private Sensor findOrCreateSensor(Sensor sensor) {
        return sensorRepository.findById(sensor.getId())
            .orElseGet(() -> sensorRepository.save(sensor));
    }

    private Condition findOrCreateCondition(Condition condition) {
        return conditionRepository
            .findByTypeAndOperationAndValue(
                condition.getType(), condition.getOperation(), condition.getValue())
            .orElseGet(() -> conditionRepository.save(condition));
    }

    private Action findOrCreateAction(Action action) {
        return actionRepository
            .findByTypeAndValue(action.getType(), action.getValue())
            .orElseGet(() -> actionRepository.save(action));
    }
}