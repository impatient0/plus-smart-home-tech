package ru.yandex.practicum.smarthometech.commerce.order.application;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartApiMapper {

    ru.yandex.practicum.smarthometech.commerce.api.dto.cart.ShoppingCartDto toCartDto(ru.yandex.practicum.smarthometech.commerce.api.dto.order.ShoppingCartDto orderCartDto);

    ru.yandex.practicum.smarthometech.commerce.api.dto.order.ShoppingCartDto toOrderDto(ru.yandex.practicum.smarthometech.commerce.api.dto.cart.ShoppingCartDto cartDto);
}