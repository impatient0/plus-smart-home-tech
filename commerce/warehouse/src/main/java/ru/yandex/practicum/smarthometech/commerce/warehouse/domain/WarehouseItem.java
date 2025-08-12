package ru.yandex.practicum.smarthometech.commerce.warehouse.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "warehouse_items")
@Getter
@Setter
@ToString
public class WarehouseItem {

    @Id
    @Column(name = "product_id")
    private UUID productId;

    @Column(name = "quantity", nullable = false)
    private Long quantity = 0L;

    @Column(name = "weight_kg", nullable = false)
    private BigDecimal weightKg;

    @Column(name = "width_m", nullable = false)
    private BigDecimal widthM;

    @Column(name = "height_m", nullable = false)
    private BigDecimal heightM;

    @Column(name = "depth_m", nullable = false)
    private BigDecimal depthM;

    @Column(name = "is_fragile", nullable = false)
    private Boolean isFragile = false;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WarehouseItem that = (WarehouseItem) o;
        return Objects.equals(productId, that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }
}