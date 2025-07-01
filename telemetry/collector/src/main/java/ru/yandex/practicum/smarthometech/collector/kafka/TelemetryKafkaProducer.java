package ru.yandex.practicum.smarthometech.collector.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelemetryKafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.sensors}")
    private String sensorTopic;

    @Value("${kafka.topic.hubs}")
    private String hubTopic;

    public void sendSensorEvent(SensorEventAvro event) {
        log.info("Sending sensor event to Kafka topic '{}': {}", sensorTopic, event);

        long eventTimestamp = event.getTimestamp().toEpochMilli();

        kafkaTemplate.send(sensorTopic,  null, eventTimestamp, event.getHubId(), event).whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Successfully sent sensor event for hubId {} to offset {}",
                    event.getHubId(), result.getRecordMetadata().offset());
            } else {
                log.error("Failed to send sensor event for hubId {}: {}", event.getHubId(), ex.getMessage());
            }
        });
    }

    public void sendHubEvent(HubEventAvro event) {
        log.info("Sending hub event to Kafka topic '{}': {}", hubTopic, event);

        long eventTimestamp = event.getTimestamp().toEpochMilli();

        kafkaTemplate.send(hubTopic, null, eventTimestamp, event.getHubId(), event).whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Successfully sent hub event for hubId {} to offset {}",
                    event.getHubId(), result.getRecordMetadata().offset());
            } else {
                log.error("Failed to send hub event for hubId {}: {}", event.getHubId(), ex.getMessage());
            }
        });
    }
}