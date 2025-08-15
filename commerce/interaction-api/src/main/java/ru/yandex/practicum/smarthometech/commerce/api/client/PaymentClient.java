package ru.yandex.practicum.smarthometech.commerce.api.client;

import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.smarthometech.commerce.api.dto.order.OrderDto;
import ru.yandex.practicum.smarthometech.commerce.api.dto.payment.PaymentDto;

@FeignClient(name = "payment", path = "/api/v1/payment")
public interface PaymentClient {

    @PostMapping
    PaymentDto createPayment(@RequestBody OrderDto order);

    @PostMapping("/totalCost")
    BigDecimal calculateTotalCost(@RequestBody OrderDto orderDto);

    @PostMapping("/productCost")
    BigDecimal calculateProductCost(@RequestBody OrderDto orderDto);

    @PostMapping("/successful")
    void setPaymentSuccessful(@RequestBody UUID paymentId);

    @PostMapping("/failed")
    void setPaymentFailed(@RequestBody UUID paymentId);
}
