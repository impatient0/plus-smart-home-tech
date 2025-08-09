package ru.yandex.practicum.smarthometech.telemetry.aggregator.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

@Service
public class SnapshotServiceImpl implements SnapshotService {

    private final Map<String, SensorsSnapshotAvro> snapshotsByHubId = new ConcurrentHashMap<>();

    @Override
    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {

        SensorsSnapshotAvro currentSnapshot = snapshotsByHubId.computeIfAbsent(event.getHubId(),
            hubId -> SensorsSnapshotAvro.newBuilder()
                .setHubId(hubId)
                .setSensorsState(new HashMap<>())
                .setTimestamp(event.getTimestamp())
                .build()
        );

        SensorStateAvro previousSensorState = currentSnapshot.getSensorsState().get(event.getId());

        if (previousSensorState != null && previousSensorState.getTimestamp().isAfter(event.getTimestamp())) {
            return Optional.empty();
        }

        if (previousSensorState != null && previousSensorState.getData().equals(event.getPayload())) {
            return Optional.empty();
        }

        SensorStateAvro newSensorState = SensorStateAvro.newBuilder()
            .setTimestamp(event.getTimestamp())
            .setData(event.getPayload())
            .build();

        currentSnapshot.getSensorsState().put(event.getId(), newSensorState);
        currentSnapshot.setTimestamp(event.getTimestamp());

        return Optional.of(currentSnapshot);
    }
}