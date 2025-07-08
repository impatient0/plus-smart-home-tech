package ru.yandex.practicum.smarthometech.collector.controller;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc.CollectorControllerImplBase;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto.PayloadCase;
import ru.yandex.practicum.smarthometech.collector.handler.HubEventHandler;
import ru.yandex.practicum.smarthometech.collector.handler.SensorEventHandler;

@GrpcService
@Slf4j
public class EventController extends CollectorControllerImplBase {

    private final Map<PayloadCase, SensorEventHandler> sensorEventHandlers;
    private final Map<HubEventProto.PayloadCase, HubEventHandler> hubEventHandlers;

    public EventController(Set<SensorEventHandler> sensorEventHandlers,
        Set<HubEventHandler> hubEventHandlers) {
        this.sensorEventHandlers = sensorEventHandlers.stream()
            .collect(Collectors.toMap(SensorEventHandler::getHandledType, Function.identity()));
        this.hubEventHandlers = hubEventHandlers.stream()
            .collect(Collectors.toMap(HubEventHandler::getHandledType, Function.identity()));
    }

    @Override
    public void collectSensorEvent(SensorEventProto request, StreamObserver<Empty> responseObserver) {
        try {
            SensorEventHandler handler = sensorEventHandlers.get(request.getPayloadCase());

            if (handler == null) {
                throw new StatusRuntimeException(Status.UNIMPLEMENTED);
            }

            sensorEventHandlers.get(request.getPayloadCase()).process(request);

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(
                Status.INTERNAL
                    .withDescription(e.getLocalizedMessage())
                    .withCause(e)
            ));
        }
    }

    @Override
    public void collectHubEvent(HubEventProto request, StreamObserver<Empty> responseObserver) {
        try {
            HubEventHandler handler = hubEventHandlers.get(request.getPayloadCase());

            if (handler == null) {
                throw new StatusRuntimeException(Status.UNIMPLEMENTED);
            }

            hubEventHandlers.get(request.getPayloadCase()).process(request);

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(
                Status.INTERNAL
                    .withDescription(e.getLocalizedMessage())
                    .withCause(e)
            ));
        }
    }
}