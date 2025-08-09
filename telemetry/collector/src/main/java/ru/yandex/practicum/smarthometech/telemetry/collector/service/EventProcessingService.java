package ru.yandex.practicum.smarthometech.telemetry.collector.service;

import ru.yandex.practicum.smarthometech.telemetry.collector.dto.HubEvent;
import ru.yandex.practicum.smarthometech.telemetry.collector.dto.SensorEvent;

public interface EventProcessingService {

    /**
     * Processes and sends a single sensor event to Kafka.
     * @param event The SensorEvent object.
     */
    void processAndSendSensorEvent(SensorEvent event);

    /**
     * Processes and sends a single hub event to Kafka.
     * @param event The HubEvent object.
     */
    void processAndSendHubEvent(HubEvent event);

}