package ru.yandex.practicum.smarthometech.commerce.delivery.application.config;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Configuration
@ConfigurationProperties(prefix = "delivery.cost")
@Getter
@Setter
@Validated
public class DeliveryCostProperties {

    @NotNull
    private BigDecimal base;

    @NotNull
    private MultiplierProperties multiplier;

    @Getter
    @Setter
    public static class MultiplierProperties {
        @NotNull
        private BigDecimal fragile;
        @NotNull
        private BigDecimal weight;
        @NotNull
        private BigDecimal volume;
        @NotNull
        private BigDecimal base;
        @NotNull
        private List<BigDecimal> warehouse;
    }
}