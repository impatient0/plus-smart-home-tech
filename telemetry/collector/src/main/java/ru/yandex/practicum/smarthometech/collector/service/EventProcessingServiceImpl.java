package ru.yandex.practicum.smarthometech.collector.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.smarthometech.collector.dto.*;
import ru.yandex.practicum.smarthometech.collector.mapper.EventMapper;

@Service
@RequiredArgsConstructor
public class EventProcessingServiceImpl implements EventProcessingService {

    private final EventMapper eventMapper;
    private final TelemetryKafkaProducer kafkaProducer;

    @Override
    public void processAndSendSensorEvent(SensorEvent event) {
        SensorEventAvro avroEvent = switch (event) {
            case LightSensorEvent e -> eventMapper.toAvro(e);
            case MotionSensorEvent e -> eventMapper.toAvro(e);
            case ClimateSensorEvent e -> eventMapper.toAvro(e);
            case SwitchSensorEvent e -> eventMapper.toAvro(e);
            case TemperatureSensorEvent e -> eventMapper.toAvro(e);
            default -> throw new IllegalArgumentException("Unsupported sensor event type: " + event.getType());
        };
        kafkaProducer.sendSensorEvent(avroEvent);
    }

    @Override
    public void processAndSendHubEvent(HubEvent event) {
        HubEventAvro avroEvent = switch (event) {
            case DeviceAddedEvent e -> eventMapper.toAvro(e);
            case DeviceRemovedEvent e -> eventMapper.toAvro(e);
            case ScenarioAddedEvent e -> eventMapper.toAvro(e);
            case ScenarioRemovedEvent e -> eventMapper.toAvro(e);
            default -> throw new IllegalArgumentException("Unsupported hub event type: " + event.getType());
        };
        kafkaProducer.sendHubEvent(avroEvent);
    }
}