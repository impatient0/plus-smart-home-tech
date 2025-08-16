package ru.yandex.practicum.smarthometech.commerce.delivery.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
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

    @Column(name = "order_id", nullable = false, unique = true)
    private UUID orderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DeliveryStatus status = DeliveryStatus.CREATED;

    @Column(name = "from_country", nullable = false)
    private String fromCountry;

    @Column(name = "from_city", nullable = false)
    private String fromCity;

    @Column(name = "from_street", nullable = false)
    private String fromStreet;

    @Column(name = "from_house", nullable = false)
    private String fromHouse;

    @Column(name = "from_flat")
    private String fromFlat;

    @Column(name = "to_country", nullable = false)
    private String toCountry;

    @Column(name = "to_city", nullable = false)
    private String toCity;

    @Column(name = "to_street", nullable = false)
    private String toStreet;

    @Column(name = "to_house", nullable = false)
    private String toHouse;

    @Column(name = "to_flat")
    private String toFlat;

    @Column(name = "delivery_weight_kg", precision = 10, scale = 3)
    private BigDecimal deliveryWeightKg;

    @Column(name = "delivery_volume_m3", precision = 10, scale = 3)
    private BigDecimal deliveryVolumeM3;

    @Column(name = "is_fragile")
    private Boolean isFragile;

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