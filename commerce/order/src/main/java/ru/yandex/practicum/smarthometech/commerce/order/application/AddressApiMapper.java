package ru.yandex.practicum.smarthometech.commerce.order.application;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AddressApiMapper {

    // --- mappings back to "true" DTO ---

    ru.yandex.practicum.smarthometech.commerce.api.dto.warehouse.AddressDto toWarehouseDto(ru.yandex.practicum.smarthometech.commerce.api.dto.delivery.AddressDto deliveryAddressDto);

    ru.yandex.practicum.smarthometech.commerce.api.dto.warehouse.AddressDto toWarehouseDto(ru.yandex.practicum.smarthometech.commerce.api.dto.order.AddressDto orderAddressDto);

    // --- mappings for Delivery service ---

    ru.yandex.practicum.smarthometech.commerce.api.dto.delivery.AddressDto toDeliveryDto(ru.yandex.practicum.smarthometech.commerce.api.dto.warehouse.AddressDto addressDto);

    ru.yandex.practicum.smarthometech.commerce.api.dto.delivery.AddressDto toDeliveryDto(ru.yandex.practicum.smarthometech.commerce.api.dto.order.AddressDto orderAddressDto);

    // --- mappings for Order service ---

    ru.yandex.practicum.smarthometech.commerce.api.dto.order.AddressDto toOrderDto(ru.yandex.practicum.smarthometech.commerce.api.dto.warehouse.AddressDto addressDto);

    ru.yandex.practicum.smarthometech.commerce.api.dto.order.AddressDto toOrderDto(ru.yandex.practicum.smarthometech.commerce.api.dto.order.AddressDto orderAddressDto);

}
