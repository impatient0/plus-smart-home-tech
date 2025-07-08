package ru.yandex.practicum.smarthometech.collector.handler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto.PayloadCase;
import ru.yandex.practicum.smarthometech.collector.kafka.TelemetryKafkaProducer;
import ru.yandex.practicum.smarthometech.collector.mapper.EventMapper;

@Component
@RequiredArgsConstructor
@Getter
@Slf4j
public class DeviceAddedHandler implements HubEventHandler {

    private final EventMapper mapper;
    private final TelemetryKafkaProducer producer;

    @Override
    public PayloadCase getHandledType() {
        return PayloadCase.DEVICE_ADDED;
    }

    @Override
    public void process(HubEventProto protoEvent) {
        var avroEvent = getMapper().toAvro(protoEvent);

        if (avroEvent != null) {
            log.info("Processing valid DEVICE_ADDED event for hubId: {}", protoEvent.getHubId());
            getProducer().sendHubEvent(avroEvent);
        } else {
            log.warn(
                "Skipping invalid DEVICE_ADDED event for hubId: {}. Reason: Unknown or "
                    + "unsupported device type.",
                protoEvent.getHubId());
        }
    }
}