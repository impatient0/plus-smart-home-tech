package ru.yandex.practicum.smarthometech.telemetry.analyzer.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.smarthometech.telemetry.analyzer.domain.entity.Condition;

public interface ConditionRepository extends JpaRepository<Condition, Long> {
    Optional<Condition> findByTypeAndOperationAndValue(String type, String operation, Integer value);
}
