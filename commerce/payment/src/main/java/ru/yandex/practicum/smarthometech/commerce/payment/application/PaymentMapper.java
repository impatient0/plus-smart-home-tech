package ru.yandex.practicum.smarthometech.commerce.payment.application;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.smarthometech.commerce.api.dto.payment.PaymentDto;
import ru.yandex.practicum.smarthometech.commerce.payment.domain.Payment;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(source = "deliveryCost", target = "deliveryTotal")
    @Mapping(source = "taxAmount", target = "feeTotal")
    PaymentDto toDto(Payment payment);
}