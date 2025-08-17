package ru.yandex.practicum.smarthometech.commerce.warehouse.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "booked_items")
@Getter
@Setter
public class BookedItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_id", nullable = false)
    private OrderBooking orderBooking;

    @NotNull
    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @NotNull
    @Column(name = "quantity", nullable = false)
    private Long quantity;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookedItem that = (BookedItem) o;
        return Objects.equals(productId, that.productId) && Objects.equals(
            this.orderBooking != null ? this.orderBooking.getOrderId() : null,
            that.orderBooking != null ? that.orderBooking.getOrderId() : null);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderBooking, productId);
    }
}