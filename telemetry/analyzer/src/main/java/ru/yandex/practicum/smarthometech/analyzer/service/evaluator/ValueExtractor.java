package ru.yandex.practicum.smarthometech.analyzer.service.evaluator;

import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import java.util.Optional;

@FunctionalInterface
public interface ValueExtractor {
    /**
     * Extracts a numeric value from a sensor's state payload (Optional<> for graceful
     * handling of invalid payloads).
     */
    Optional<Integer> extract(SensorStateAvro sensorState);
}