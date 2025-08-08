package ru.yandex.practicum.smarthometech.commerce.cart.infrastructure;

import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignClientConfiguration {

    @Bean("warehouseErrorDecoder")
    public ErrorDecoder warehouseErrorDecoder() {
        return new WarehouseErrorDecoder();
    }
}