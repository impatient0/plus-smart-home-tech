package ru.yandex.practicum.smarthometech.analyzer.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.smarthometech.analyzer.domain.entity.Action;

public interface ActionRepository extends JpaRepository<Action, Long> {
    Optional<Action> findByTypeAndValue(String type, Integer value);
}
