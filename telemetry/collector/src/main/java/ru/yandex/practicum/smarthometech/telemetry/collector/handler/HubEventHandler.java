package ru.yandex.practicum.smarthometech.telemetry.collector.handler;

import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.smarthometech.telemetry.collector.kafka.TelemetryKafkaProducer;
import ru.yandex.practicum.smarthometech.telemetry.collector.mapper.EventMapper;

public interface HubEventHandler extends AbstractEventHandler<HubEventProto, HubEventAvro> {

    /**
     * This method must be overridden in the concrete class.
     * @return The specific message type this handler is responsible for.
     */
    HubEventProto.PayloadCase getHandledType();

    /**
     * This is a default method providing a complete implementation of the main processing logic;
     * can be overridden if more complex logic is required.
     * @param protoEvent The incoming hub event from gRPC.
     */
    @Override
    default void process(HubEventProto protoEvent) {
        EventMapper mapper = getMapper();
        TelemetryKafkaProducer producer = getProducer();

        var avroEvent = mapper.toAvro(protoEvent);
        producer.sendHubEvent(avroEvent);
    }

    EventMapper getMapper();
    TelemetryKafkaProducer getProducer();
}
