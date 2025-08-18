package ru.yandex.practicum.smarthometech.commerce.order.application;

import org.mapstruct.Mapper;
import ru.yandex.practicum.smarthometech.commerce.api.dto.order.OrderDto;
import ru.yandex.practicum.smarthometech.commerce.api.dto.order.OrderDto.StateEnum;
import ru.yandex.practicum.smarthometech.commerce.order.domain.Order;
import ru.yandex.practicum.smarthometech.commerce.order.domain.OrderItem;

import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    default OrderDto toDto(Order order) {
        if (order == null) {
            return null;
        }

        OrderDto dto = new OrderDto();
        dto.setOrderId(order.getOrderId());
        dto.setShoppingCartId(order.getShoppingCartId());
        dto.setPaymentId(order.getPaymentId());
        dto.setDeliveryId(order.getDeliveryId());
        dto.setState(StateEnum.fromValue(order.getStatus().name()));

        dto.setProductPrice(order.getTotalPrice().subtract(order.getDeliveryPrice()).subtract(order.getFee()));
        dto.setDeliveryPrice(order.getDeliveryPrice());
        dto.setTotalPrice(order.getTotalPrice());

        Map<String, Long> products = order.getItems().stream().collect(
            Collectors.toMap(item -> item.getProductId().toString(), OrderItem::getQuantity));
        dto.setProducts(products);

        return dto;
    }
}