package ru.yandex.practicum.smarthometech.commerce.order.infrastructure;

import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignClientConfiguration {
    @Bean("warehouseErrorDecoder")
    public ErrorDecoder warehouseErrorDecoder() { return new WarehouseErrorDecoder(); }

    @Bean("deliveryErrorDecoder")
    public ErrorDecoder deliveryErrorDecoder() { return new DeliveryErrorDecoder(); }

    @Bean("paymentErrorDecoder")
    public ErrorDecoder paymentErrorDecoder() { return new PaymentErrorDecoder(); }
}