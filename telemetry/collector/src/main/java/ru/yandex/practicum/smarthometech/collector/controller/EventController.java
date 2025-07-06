package ru.yandex.practicum.smarthometech.collector.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.smarthometech.collector.dto.HubEvent;
import ru.yandex.practicum.smarthometech.collector.dto.SensorEvent;
import ru.yandex.practicum.smarthometech.collector.service.EventProcessingService;

@Slf4j
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventProcessingService eventProcessingService;

    @PostMapping("/sensors")
    public void handleSensorEvent(@Valid @RequestBody SensorEvent event) {
        log.info("Received sensor event: {}", event);
        eventProcessingService.processAndSendSensorEvent(event);
    }

    @PostMapping("/hubs")
    public void collectHubEvent(@Valid @RequestBody HubEvent event) {
        log.info("Received hub event: {}", event);
        eventProcessingService.processAndSendHubEvent(event);
    }

}
