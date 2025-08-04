package ru.yandex.practicum.smarthometech.telemetry.aggregator.service;

import java.util.Optional;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

public interface SnapshotService {

    /**
     * Processes a single sensor event and updates the corresponding hub's snapshot if necessary.
     *
     * @param event The SensorEvent object.
     * @return An Optional containing the updated snapshot if a change was made,
     *         or an empty Optional otherwise.
     */
    Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event);

}
