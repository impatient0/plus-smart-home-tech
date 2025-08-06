package ru.yandex.practicum.smarthometech.commerce.store.domain.entity;

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
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import ru.yandex.practicum.smarthometech.commerce.api.dto.store.ProductCategory;
import ru.yandex.practicum.smarthometech.commerce.api.dto.store.ProductState;
import ru.yandex.practicum.smarthometech.commerce.api.dto.store.QuantityState;

@Entity
@Table(name = "products", schema = "shopping_store")
@Getter
@Setter
@ToString
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "product_id")
    private UUID productId;

    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "description", columnDefinition = "TEXT")
    String description;

    @Column(name = "image_src", nullable = false)
    String imageSrc;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "quantity_state", nullable = false, columnDefinition = "shopping_store.quantity_state")
    private QuantityState quantityState;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "product_state", nullable = false, columnDefinition = "shopping_store.product_state")
    private ProductState productState;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "category", nullable = false, columnDefinition = "shopping_store.product_category")
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
        return name.equals(product.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

}
