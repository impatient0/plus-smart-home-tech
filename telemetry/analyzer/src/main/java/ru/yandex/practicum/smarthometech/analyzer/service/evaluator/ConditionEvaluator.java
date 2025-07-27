package ru.yandex.practicum.smarthometech.analyzer.service.evaluator;

import java.util.function.Function;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.smarthometech.analyzer.domain.entity.Condition;

import java.util.Map;
import java.util.Optional;

@Component
public class ConditionEvaluator {

    private static final ValueExtractor TEMPERATURE_EXTRACTOR = state -> {
        Object data = state.getData();
        if (data instanceof TemperatureSensorAvro tempSensor) {
            return Optional.of(tempSensor.getTemperatureC());
        }
        if (data instanceof ClimateSensorAvro climateSensor) {
            return Optional.of(climateSensor.getTemperatureC());
        }
        return Optional.empty();
    };

    private static final Map<String, ValueExtractor> EXTRACTORS = Map.of(
        "MOTION", state -> extract(state, MotionSensorAvro.class, avro -> avro.getMotion() ? 1 : 0),
        "LUMINOSITY", state -> extract(state, LightSensorAvro.class, LightSensorAvro::getLuminosity),
        "TEMPERATURE", TEMPERATURE_EXTRACTOR,
        "CO2LEVEL", state -> extract(state, ClimateSensorAvro.class, ClimateSensorAvro::getCo2Level),
        "HUMIDITY", state -> extract(state, ClimateSensorAvro.class, ClimateSensorAvro::getHumidity),
        "SWITCH", state -> extract(state, SwitchSensorAvro.class, avro -> avro.getState() ? 1 : 0)
    );

    public boolean evaluate(Condition condition, SensorStateAvro sensorState) {
        ValueExtractor extractor = EXTRACTORS.get(condition.getType());
        if (extractor == null) {
            return false;
        }

        Optional<Integer> actualValueOpt = extractor.extract(sensorState);

        return actualValueOpt.map(actualValue ->
            compare(actualValue, condition.getOperation(), condition.getValue())
        ).orElse(false);
    }

    private static <T> Optional<Integer> extract(SensorStateAvro state, Class<T> clazz, Function<T, Integer> mapper) {
        if (clazz.isInstance(state.getData())) {
            return Optional.of(mapper.apply(clazz.cast(state.getData())));
        }
        return Optional.empty();
    }

    private boolean compare(int actualValue, String operation, int conditionValue) {
        return switch (operation) {
            case "EQUALS" -> actualValue == conditionValue;
            case "GREATER_THAN" -> actualValue > conditionValue;
            case "LOWER_THAN" -> actualValue < conditionValue;
            default -> false;
        };
    }
}