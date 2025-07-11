package ru.yandex.practicum.smarthometech.collector.handler;

import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.smarthometech.collector.mapper.EventMapper;
import ru.yandex.practicum.smarthometech.collector.kafka.TelemetryKafkaProducer;

public interface SensorEventHandler extends AbstractEventHandler<SensorEventProto, SensorEventAvro> {

    /**
     * This method must be overridden in the concrete class.
     * @return The specific message type this handler is responsible for.
     */
    SensorEventProto.PayloadCase getHandledType();

    /**
     * This is a default method providing a complete implementation of the main processing logic;
     * can be overridden if more complex logic is required.
     * @param protoEvent The incoming sensor event from gRPC.
     */
    @Override
    default void process(SensorEventProto protoEvent) {
        EventMapper mapper = getMapper();
        TelemetryKafkaProducer producer = getProducer();

        var avroEvent = mapper.toAvro(protoEvent);
        producer.sendSensorEvent(avroEvent);
    }

    EventMapper getMapper();
    TelemetryKafkaProducer getProducer();
}