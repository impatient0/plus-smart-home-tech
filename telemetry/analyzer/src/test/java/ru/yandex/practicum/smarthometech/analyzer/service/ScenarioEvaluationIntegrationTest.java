package ru.yandex.practicum.smarthometech.analyzer.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.avro.generic.GenericContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.kafka.telemetry.serializer.BaseAvroSerializer;
import ru.yandex.practicum.smarthometech.analyzer.client.HubRouterClient;

@SpringBootTest
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9093", "port=9093" },
    topics = { "${kafka.topic.hubs}", "${kafka.topic.snapshots}" })
@DirtiesContext
class ScenarioEvaluationIntegrationTest {

    @Autowired
    private KafkaTemplate<String, byte[]> kafkaTemplate;

    @MockitoSpyBean
    private HubRouterClient hubRouterClient;

    @Autowired
    private KafkaListenerEndpointRegistry registry;

    private BaseAvroSerializer<GenericContainer> avroSerializer;

    @BeforeEach
    void setUp() {
        avroSerializer = new BaseAvroSerializer<>();
        ContainerTestUtils.waitForAssignment(
            Objects.requireNonNull(registry.getListenerContainer("hub-events-listener")), 1);
        ContainerTestUtils.waitForAssignment(
            Objects.requireNonNull(registry.getListenerContainer("snapshots-listener")), 1);
    }

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.producer.value-serializer",
            () -> "org.apache.kafka.common.serialization.ByteArraySerializer");
    }

    @Test
    void shouldTriggerScenarioAndCallHubRouterWhenConditionsAreMet() {
        // ... ARRANGE ...
        String hubId = "test-hub-1";
        String sensorId = "motion-sensor-1";
        String scenarioName = "turn on light on motion";

        var addedEvent = createScenarioAddedEvent(hubId, sensorId, scenarioName);
        var snapshot = createTriggeringSnapshot(hubId, sensorId);

        byte[] addedEventBytes = avroSerializer.serialize("telemetry.hubs.v1", addedEvent);
        kafkaTemplate.send("telemetry.hubs.v1", hubId, addedEventBytes);

        try {
            Thread.sleep(500); // Wait 500 ms
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        byte[] snapshotBytes = avroSerializer.serialize("telemetry.snapshots.v1", snapshot);
        kafkaTemplate.send("telemetry.snapshots.v1", hubId, snapshotBytes);

        // --- ACT & ASSERT ---
        Mockito.verify(hubRouterClient, Mockito.timeout(5000).times(1))
            .executeAction(eq(hubId), eq(scenarioName), any(), any());
    }

    private HubEventAvro createScenarioAddedEvent(String hubId, String sensorId, String scenarioName) {
        var condition = ScenarioConditionAvro.newBuilder()
            .setSensorId(sensorId)
            .setType(ConditionTypeAvro.MOTION)
            .setOperation(ConditionOperationAvro.EQUALS)
            .setValue(1)
            .build();

        var action = DeviceActionAvro.newBuilder()
            .setSensorId("light-1")
            .setType(ActionTypeAvro.ACTIVATE)
            .build();

        var payload = ScenarioAddedEventAvro.newBuilder()
            .setName(scenarioName)
            .setConditions(List.of(condition))
            .setActions(List.of(action))
            .build();

        return HubEventAvro.newBuilder()
            .setHubId(hubId)
            .setTimestamp(Instant.now())
            .setPayload(payload)
            .build();
    }

    private SensorsSnapshotAvro createTriggeringSnapshot(String hubId, String sensorId) {
        var motionSensor = MotionSensorAvro.newBuilder()
            .setMotion(true)
            .setLinkQuality(99)
            .setVoltage(220)
            .build();

        var sensorState = SensorStateAvro.newBuilder()
            .setTimestamp(Instant.now())
            .setData(motionSensor)
            .build();

        return SensorsSnapshotAvro.newBuilder()
            .setHubId(hubId)
            .setTimestamp(Instant.now())
            .setSensorsState(Map.of(sensorId, sensorState))
            .build();
    }
}