package ru.yandex.practicum.smarthometech.commerce.delivery.application;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.smarthometech.commerce.api.dto.delivery.DeliveryDto;
import ru.yandex.practicum.smarthometech.commerce.delivery.domain.Delivery;

@Mapper(componentModel = "spring")
public interface DeliveryMapper {

    @Mapping(target = "fromAddress.country", source = "fromCountry")
    @Mapping(target = "fromAddress.city", source = "fromCity")
    @Mapping(target = "fromAddress.street", source = "fromStreet")
    @Mapping(target = "fromAddress.house", source = "fromHouse")
    @Mapping(target = "fromAddress.flat", source = "fromFlat")
    @Mapping(target = "toAddress.country", source = "toCountry")
    @Mapping(target = "toAddress.city", source = "toCity")
    @Mapping(target = "toAddress.street", source = "toStreet")
    @Mapping(target = "toAddress.house", source = "toHouse")
    @Mapping(target = "toAddress.flat", source = "toFlat")
    @Mapping(target = "deliveryState", source = "status")
    DeliveryDto toDto(Delivery delivery);

    @Mapping(target = "fromCountry", source = "fromAddress.country")
    @Mapping(target = "fromCity", source = "fromAddress.city")
    @Mapping(target = "fromStreet", source = "fromAddress.street")
    @Mapping(target = "fromHouse", source = "fromAddress.house")
    @Mapping(target = "fromFlat", source = "fromAddress.flat")
    @Mapping(target = "toCountry", source = "toAddress.country")
    @Mapping(target = "toCity", source = "toAddress.city")
    @Mapping(target = "toStreet", source = "toAddress.street")
    @Mapping(target = "toHouse", source = "toAddress.house")
    @Mapping(target = "toFlat", source = "toAddress.flat")
    @Mapping(target = "status", source = "deliveryState")
    Delivery toEntity(DeliveryDto deliveryDto);
}