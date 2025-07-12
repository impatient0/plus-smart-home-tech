package ru.yandex.practicum.smarthometech.aggregator.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

@Component
@RequiredArgsConstructor
@Slf4j
public class SensorSnapshotKafkaProducer {

    private final KafkaTemplate<String, SensorsSnapshotAvro> kafkaTemplate;

    @Value("${kafka.topic.snapshots}")
    private String snapshotsTopic;

    public void send(SensorsSnapshotAvro snapshot) {
        log.info("Producing new snapshot for hubId: {}", snapshot.getHubId());
        kafkaTemplate.send(snapshotsTopic, snapshot.getHubId(), snapshot)
            .whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Successfully sent new snapshot for hubId {} to offset {}",
                        snapshot.getHubId(), result.getRecordMetadata().offset());
                } else {
                    log.error("Failed to send new snapshot for hubId {}: {}", snapshot.getHubId(),
                        ex.getMessage());
                }
            });
    }
}