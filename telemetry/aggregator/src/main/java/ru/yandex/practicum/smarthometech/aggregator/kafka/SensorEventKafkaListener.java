package ru.yandex.practicum.smarthometech.aggregator.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.smarthometech.aggregator.service.SnapshotService;

@Component
@RequiredArgsConstructor
@Slf4j
public class SensorEventKafkaListener {

    private final SnapshotService snapshotService;
    private final SensorSnapshotKafkaProducer snapshotProducer;

    @KafkaListener(
        topics = "${kafka.topic.sensors}",
        groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(SensorEventAvro event) {
        log.debug("Received sensor event: {}", event);

        snapshotService.updateState(event).ifPresentOrElse(snapshotProducer::send,
            () -> log.debug("New snapshot was not created."));
    }
}