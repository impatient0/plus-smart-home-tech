package ru.yandex.practicum.smarthometech.analyzer.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.smarthometech.analyzer.domain.entity.Scenario;
import ru.yandex.practicum.smarthometech.analyzer.domain.entity.ScenarioCondition;
import ru.yandex.practicum.smarthometech.analyzer.repository.ScenarioRepository;
import ru.yandex.practicum.smarthometech.analyzer.service.evaluator.ConditionEvaluator;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScenarioEvaluationService {

    private final ScenarioRepository scenarioRepository;
    private final ConditionEvaluator conditionEvaluator;
    // TODO: gRPC client integration
    // private final HubRouterClient hubRouterClient;

    public void evaluate(SensorsSnapshotAvro snapshot) {
        String hubId = snapshot.getHubId();
        log.debug("Evaluating snapshot for hubId: {}", hubId);

        List<Scenario> scenarios = scenarioRepository.findByHubId(hubId);

        scenarios.stream()
            .filter(scenario -> allConditionsMet(scenario, snapshot))
            .flatMap(scenario -> scenario.getScenarioActions().stream())
            .forEach(action -> {
                // TODO: gRPC client integration
                // hubRouterClient.executeAction(hubId, scenario.getName(), action);
            });
    }

    private boolean allConditionsMet(Scenario scenario, SensorsSnapshotAvro snapshot) {
        return scenario.getScenarioConditions()
            .stream()
            .allMatch(scenarioCondition -> isConditionMet(scenarioCondition, snapshot));
    }

    private boolean isConditionMet(ScenarioCondition scenarioCondition,
        SensorsSnapshotAvro snapshot) {

        SensorStateAvro sensorState = snapshot.getSensorsState().get(scenarioCondition.getSensor().getId());

        if (sensorState == null) {
            return false;
        }

        return conditionEvaluator.evaluate(scenarioCondition.getCondition(), sensorState);
    }
}