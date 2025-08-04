package ru.yandex.practicum.smarthometech.store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import ru.yandex.practicum.smarthometech.commerce.api.client.ShoppingCartClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ShoppingStoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingCartClient.class, args);
    }

}
