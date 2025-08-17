package ru.yandex.practicum.smarthometech.commerce.warehouse.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "order_bookings")
@Getter
@Setter
public class OrderBooking {

    @Id
    @NotNull
    @Column(name = "order_id")
    private UUID orderId;

    @Column(name = "delivery_id")
    private UUID deliveryId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BookingStatus status = BookingStatus.ASSEMBLED;

    @NotNull
    @OneToMany(mappedBy = "orderBooking", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<BookedItem> items = new HashSet<>();

    public void addItem(BookedItem item) {
        items.add(item);
        item.setOrderBooking(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderBooking that = (OrderBooking) o;
        return Objects.equals(orderId, that.orderId) && Objects.equals(deliveryId, that.deliveryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, deliveryId);
    }
}