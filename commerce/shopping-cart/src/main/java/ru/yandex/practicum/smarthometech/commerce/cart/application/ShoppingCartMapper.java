package ru.yandex.practicum.smarthometech.commerce.cart.application;

import org.mapstruct.Mapper;
import ru.yandex.practicum.smarthometech.commerce.api.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.smarthometech.commerce.cart.domain.CartItem;
import ru.yandex.practicum.smarthometech.commerce.cart.domain.ShoppingCart;

import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ShoppingCartMapper {

    default ShoppingCartDto toDto(ShoppingCart cart) {
        if (cart == null) {
            return null;
        }

        ShoppingCartDto dto = new ShoppingCartDto();
        dto.setShoppingCartId(cart.getShoppingCartId());

        Map<String, Long> productsMap = cart.getItems().stream()
            .collect(Collectors.toMap(
                item -> item.getProductId().toString(),
                CartItem::getQuantity
            ));
        dto.setProducts(productsMap);

        return dto;
    }
}