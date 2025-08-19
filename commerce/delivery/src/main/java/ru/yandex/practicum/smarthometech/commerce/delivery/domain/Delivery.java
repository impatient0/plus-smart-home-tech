package ru.yandex.practicum.smarthometech.commerce.delivery.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "deliveries")
@Getter
@Setter
@ToString
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "delivery_id")
    private UUID deliveryId;

    @NotNull
    @Column(name = "order_id", nullable = false, unique = true)
    private UUID orderId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DeliveryStatus status = DeliveryStatus.CREATED;

    @NotNull
    @Column(name = "from_country", nullable = false)
    private String fromCountry;

    @NotNull
    @Column(name = "from_city", nullable = false)
    private String fromCity;

    @NotNull
    @Column(name = "from_street", nullable = false)
    private String fromStreet;

    @NotNull
    @Column(name = "from_house", nullable = false)
    private String fromHouse;

    @Column(name = "from_flat")
    private String fromFlat;

    @NotNull
    @Column(name = "to_country", nullable = false)
    private String toCountry;

    @NotNull
    @Column(name = "to_city", nullable = false)
    private String toCity;

    @NotNull
    @Column(name = "to_street", nullable = false)
    private String toStreet;

    @NotNull
    @Column(name = "to_house", nullable = false)
    private String toHouse;

    @Column(name = "to_flat")
    private String toFlat;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Delivery delivery = (Delivery) o;
        return Objects.equals(orderId, delivery.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }
}