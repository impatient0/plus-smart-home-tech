package ru.yandex.practicum.smarthometech.analyzer.domain.entity;

import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode
@Getter
@Setter
public class ScenarioActionId implements Serializable {
    private Long scenario;
    private String sensor;
    private Long action;
}
