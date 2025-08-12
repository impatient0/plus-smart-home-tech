package ru.yandex.practicum.smarthometech.commerce.store.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "products")
@Getter
@Setter
@ToString
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "product_id")
    private UUID productId;

    @Column(name = "name", nullable = false)
    String productName;

    @Column(name = "description", columnDefinition = "TEXT")
    String description;

    @Column(name = "image_src")
    String imageSrc;

    @Enumerated(EnumType.STRING)
    @Column(name = "quantity_state", nullable = false)
    private QuantityState quantityState;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_state", nullable = false)
    private ProductState productState;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private ProductCategory productCategory;

    @Column(name = "price", nullable = false)
    BigDecimal price;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        if (productId != null && product.productId != null) {
            return productId.equals(product.productId);
        }
        return productName.equals(product.productName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productName);
    }

}
