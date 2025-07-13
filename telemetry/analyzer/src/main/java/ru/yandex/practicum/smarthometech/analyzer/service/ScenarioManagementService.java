package ru.yandex.practicum.smarthometech.analyzer.service;

import jakarta.transaction.Transactional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.smarthometech.analyzer.domain.entity.Action;
import ru.yandex.practicum.smarthometech.analyzer.domain.entity.Condition;
import ru.yandex.practicum.smarthometech.analyzer.domain.entity.Scenario;
import ru.yandex.practicum.smarthometech.analyzer.mapper.EventMapper;
import ru.yandex.practicum.smarthometech.analyzer.repository.ActionRepository;
import ru.yandex.practicum.smarthometech.analyzer.repository.ConditionRepository;
import ru.yandex.practicum.smarthometech.analyzer.repository.ScenarioRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class ScenarioManagementService {

    private final ScenarioRepository scenarioRepository;
    private final ConditionRepository conditionRepository;
    private final ActionRepository actionRepository;
    private final EventMapper eventMapper;

    public void saveOrUpdateScenario(HubEventAvro event) {
        if (!(event.getPayload() instanceof ScenarioAddedEventAvro addedEvent)) {
            return;
        }

        Scenario transientScenario = eventMapper.toScenarioEntity(event.getHubId(), addedEvent);

        Set<Condition> managedConditions = transientScenario.getConditions().stream()
            .map(this::findOrCreateCondition)
            .collect(Collectors.toSet());

        Set<Action> managedActions = transientScenario.getActions().stream()
            .map(this::findOrCreateAction)
            .collect(Collectors.toSet());

        Scenario scenario = scenarioRepository
            .findByHubIdAndName(transientScenario.getHubId(), transientScenario.getName())
            .orElse(transientScenario);

        scenario.setConditions(managedConditions);
        scenario.setActions(managedActions);

        scenarioRepository.save(scenario);
    }

    private Condition findOrCreateCondition(Condition condition) {
        return conditionRepository
            .findByTypeAndOperationAndValue(condition.getType(), condition.getOperation(), condition.getValue())
            .orElseGet(() -> conditionRepository.save(condition));
    }

    private Action findOrCreateAction(Action action) {
        return actionRepository
            .findByTypeAndValue(action.getType(), action.getValue())
            .orElseGet(() -> actionRepository.save(action));
    }
}
