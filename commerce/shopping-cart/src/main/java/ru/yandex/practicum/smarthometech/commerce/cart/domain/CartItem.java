package ru.yandex.practicum.smarthometech.commerce.cart.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "cart_items", schema = "shopping_cart")
@Getter
@Setter
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The link back to the parent ShoppingCart
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shopping_cart_id", nullable = false)
    private ShoppingCart shoppingCart;

    @NotNull
    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @NotNull
    @Column(name = "quantity", nullable = false)
    private Long quantity;

    // A robust equals/hashCode is crucial for managing items in a Set
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartItem cartItem = (CartItem) o;
        // Two items are the same if they are for the same product in the same cart
        return Objects.equals(shoppingCart, cartItem.shoppingCart) &&
            Objects.equals(productId, cartItem.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shoppingCart, productId);
    }
}