package ru.yandex.practicum.smarthometech.analyzer.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.smarthometech.analyzer.mapper.EventMapper;
import ru.yandex.practicum.smarthometech.analyzer.repository.ScenarioRepository;
import ru.yandex.practicum.smarthometech.analyzer.repository.SensorRepository;
import ru.yandex.practicum.smarthometech.analyzer.service.ScenarioManagementService;

@Component
@RequiredArgsConstructor
@Slf4j
public class HubEventKafkaListener {

    private final SensorRepository sensorRepository;
    private final ScenarioRepository scenarioRepository;
    private final ScenarioManagementService scenarioService;
    private final EventMapper eventMapper;

    @KafkaListener(id = "hub-events-listener",
        containerFactory = "hubEventsContainerFactory",
        topics = "${kafka.topic.hubs}",
        groupId = "analyzer-hubs-group")
    public void consumeHubEvent(HubEventAvro event) {
        log.info("Received hub event for hubId {}: {}", event.getHubId(),
            event.getPayload().getClass().getSimpleName());

        switch (event.getPayload()) {
            case DeviceAddedEventAvro added -> {
                sensorRepository.save(eventMapper.toSensorEntity(event.getHubId(), added));
            }
            case DeviceRemovedEventAvro removed -> {
                sensorRepository.deleteById(removed.getId());
            }
            case ScenarioAddedEventAvro added -> {
                scenarioService.saveOrUpdateScenario(event);
            }
            case ScenarioRemovedEventAvro removed -> {
                scenarioRepository.findByHubIdAndName(event.getHubId(), removed.getName())
                    .ifPresent(scenarioRepository::delete);
            }
            default -> log.warn("Received unhandled hub event payload type: {}",
                event.getPayload().getClass().getSimpleName());
        }
    }
}
