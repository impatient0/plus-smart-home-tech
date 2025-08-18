package ru.yandex.practicum.smarthometech.commerce.order.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "order_id")
    private UUID orderId;

    @NotBlank
    @Column(name = "username", nullable = false)
    private String username;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status = OrderStatus.NEW;

    @NotNull
    @Column(name = "shopping_cart_id", nullable = false)
    private UUID shoppingCartId;

    @Column(name = "payment_id")
    private UUID paymentId;

    @Column(name = "delivery_id")
    private UUID deliveryId;

    @NotNull
    @Column(name = "delivery_address_street", nullable = false)
    private String deliveryAddressStreet; // stored for history

    @Column(name = "fee", precision = 10, scale = 2)
    private BigDecimal fee;

    @Column(name = "delivery_price", precision = 10, scale = 2)
    private BigDecimal deliveryPrice;

    @Column(name = "total_price", precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<OrderItem> items = new HashSet<>();

    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order that = (Order) o;
        if (orderId != null && that.orderId != null) {
            return Objects.equals(orderId, that.orderId);
        }
        return Objects.equals(shoppingCartId, that.shoppingCartId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shoppingCartId);
    }
}