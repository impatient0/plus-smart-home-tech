package ru.yandex.practicum.smarthometech.analyzer.client;

import com.google.protobuf.Timestamp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequestProto;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc.HubRouterControllerBlockingStub;
import ru.yandex.practicum.smarthometech.analyzer.domain.entity.Action;
import ru.yandex.practicum.smarthometech.analyzer.domain.entity.Sensor;
import ru.yandex.practicum.smarthometech.analyzer.mapper.EventMapper;

import java.time.Instant;

@Service
@Slf4j
@RequiredArgsConstructor
public class HubRouterClient {

    @GrpcClient("hub-router")
    private HubRouterControllerBlockingStub hubRouterStub;

    private final EventMapper eventMapper;

    public void executeAction(String hubId, String scenarioName, Sensor sensor, Action action) {
        try {
            log.info("Executing action '{}' on sensor '{}' for scenario '{}'",
                action.getType(), sensor.getId(), scenarioName);

            var actionProto = eventMapper.toProto(action, sensor);

            Instant now = Instant.now();
            DeviceActionRequestProto request = DeviceActionRequestProto.newBuilder()
                .setHubId(hubId)
                .setScenarioName(scenarioName)
                .setAction(actionProto)
                .setTimestamp(Timestamp.newBuilder().setSeconds(now.getEpochSecond()).setNanos(now.getNano()).build())
                .build();

            hubRouterStub.handleDeviceAction(request);

            log.info("Successfully executed action for scenario '{}'", scenarioName);

        } catch (Exception e) {
            log.error("Failed to execute gRPC action for scenario '{}' on hub '{}'",
                scenarioName, hubId, e);
        }
    }
}