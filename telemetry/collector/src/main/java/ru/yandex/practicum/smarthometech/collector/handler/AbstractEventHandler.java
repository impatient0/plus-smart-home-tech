package ru.yandex.practicum.smarthometech.collector.handler;

public interface AbstractEventHandler<P, A> {

    /**
     * The main processing logic. It maps the proto event to avro
     * and then publishes it.
     * @param protoEvent The incoming event from gRPC.
     */
    void process(P protoEvent);

}
