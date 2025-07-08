package ru.yandex.practicum.smarthometech.collector.handler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto.PayloadCase;
import ru.yandex.practicum.smarthometech.collector.kafka.TelemetryKafkaProducer;
import ru.yandex.practicum.smarthometech.collector.mapper.EventMapper;

@Component
@RequiredArgsConstructor
@Getter
public class DeviceAddedHandler implements HubEventHandler {

    private final EventMapper mapper;
    private final TelemetryKafkaProducer producer;

    @Override
    public PayloadCase getHandledType() {
        return PayloadCase.DEVICE_ADDED;
    }
}