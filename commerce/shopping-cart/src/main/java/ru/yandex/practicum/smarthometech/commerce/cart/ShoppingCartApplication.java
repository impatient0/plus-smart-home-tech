package ru.yandex.practicum.smarthometech.commerce.cart;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.DIRECT;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "ru.yandex.practicum.smarthometech.commerce.api.client")
@EnableSpringDataWebSupport(pageSerializationMode = DIRECT)
public class ShoppingCartApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingCartApplication.class, args);
    }

}
