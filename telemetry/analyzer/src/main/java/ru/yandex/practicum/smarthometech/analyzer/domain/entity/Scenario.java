package ru.yandex.practicum.smarthometech.analyzer.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "scenarios", uniqueConstraints = {
    @UniqueConstraint(name = "uc_scenario_hub_id_name", columnNames = {"hub_id", "name"})
})
@Getter
@Setter
public class Scenario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "hub_id", nullable = false)
    private String hubId;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(name = "scenario_conditions",
        joinColumns = @JoinColumn(name = "scenario_id"),
        inverseJoinColumns = @JoinColumn(name = "condition_id")
    )
    private Set<Condition> conditions = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(name = "scenario_actions",
        joinColumns = @JoinColumn(name = "scenario_id"),
        inverseJoinColumns = @JoinColumn(name = "action_id")
    )
    private Set<Action> actions = new HashSet<>();
}