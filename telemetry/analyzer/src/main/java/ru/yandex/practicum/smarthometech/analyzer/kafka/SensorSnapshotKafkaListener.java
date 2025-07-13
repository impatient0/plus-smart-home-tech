package ru.yandex.practicum.smarthometech.analyzer.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.smarthometech.analyzer.service.ScenarioEvaluationService;

@Component
@RequiredArgsConstructor
@Slf4j
public class SensorSnapshotKafkaListener {

    private final ScenarioEvaluationService evaluationService;

    @KafkaListener(topics = "${kafka.topic.snapshots}", groupId = "analyzer-snapshots-group")
    public void consumeSnapshot(SensorsSnapshotAvro snapshot) {
        log.debug("Received snapshot for hubId: {}", snapshot.getHubId());

        evaluationService.evaluate(snapshot);
    }
}
