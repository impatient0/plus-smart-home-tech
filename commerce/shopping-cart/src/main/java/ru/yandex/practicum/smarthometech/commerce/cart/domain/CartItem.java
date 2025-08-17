package ru.yandex.practicum.smarthometech.commerce.cart.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "cart_items")
@Getter
@Setter
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shopping_cart_id", nullable = false)
    private ShoppingCart shoppingCart;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "quantity", nullable = false)
    private Long quantity;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartItem that = (CartItem) o;
        return Objects.equals(this.productId, that.productId) && Objects.equals(
            this.shoppingCart != null ? this.shoppingCart.getShoppingCartId() : null,
            that.shoppingCart != null ? that.shoppingCart.getShoppingCartId() : null);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.shoppingCart != null ? this.shoppingCart.getShoppingCartId() : null,
            this.productId);
    }
}