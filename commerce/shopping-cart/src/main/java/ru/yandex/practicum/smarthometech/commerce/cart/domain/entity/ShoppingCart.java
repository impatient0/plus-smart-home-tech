package ru.yandex.practicum.smarthometech.commerce.cart.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import ru.yandex.practicum.smarthometech.commerce.cart.domain.enums.CartStatus;

@Entity
@Table(name = "shopping_carts", schema = "shopping_cart")
@Getter
@Setter
public class ShoppingCart {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "shopping_cart_id")
    private UUID shoppingCartId;

    @NotBlank
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CartStatus status = CartStatus.ACTIVE;

    // This is the parent side of the relationship.
    // CascadeType.ALL means if we save the cart, its items are also saved.
    // orphanRemoval=true means if we remove an item from this Set, it gets deleted from the database.
    @OneToMany(mappedBy = "shoppingCart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<CartItem> items = new HashSet<>();

    // Helper method to easily add an item
    public void addItem(CartItem item) {
        items.add(item);
        item.setShoppingCart(this);
    }

    // A business key (username) is good for equals/hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShoppingCart that = (ShoppingCart) o;
        return Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}