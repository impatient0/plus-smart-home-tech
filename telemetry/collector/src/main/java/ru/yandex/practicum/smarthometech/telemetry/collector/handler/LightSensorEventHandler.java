package ru.yandex.practicum.smarthometech.telemetry.collector.handler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto.PayloadCase;
import ru.yandex.practicum.smarthometech.telemetry.collector.kafka.TelemetryKafkaProducer;
import ru.yandex.practicum.smarthometech.telemetry.collector.mapper.EventMapper;

@Component
@RequiredArgsConstructor
@Getter
public class LightSensorEventHandler implements SensorEventHandler {

    private final EventMapper mapper;
    private final TelemetryKafkaProducer producer;

    @Override
    public PayloadCase getHandledType() {
        return PayloadCase.LIGHT_SENSOR_EVENT;
    }
}