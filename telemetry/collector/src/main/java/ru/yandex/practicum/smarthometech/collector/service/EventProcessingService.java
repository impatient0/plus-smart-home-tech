package ru.yandex.practicum.smarthometech.collector.service;

import ru.yandex.practicum.smarthometech.collector.dto.HubEvent;
import ru.yandex.practicum.smarthometech.collector.dto.SensorEvent;

public interface EventProcessingService {

    void processAndSendSensorEvent(SensorEvent event);

    void processAndSendHubEvent(HubEvent event);
}