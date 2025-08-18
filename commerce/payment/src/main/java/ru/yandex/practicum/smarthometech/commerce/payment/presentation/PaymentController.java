package ru.yandex.practicum.smarthometech.commerce.payment.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.smarthometech.commerce.api.client.PaymentClient;
import ru.yandex.practicum.smarthometech.commerce.api.dto.order.OrderDto;
import ru.yandex.practicum.smarthometech.commerce.api.dto.payment.PaymentDto;
import ru.yandex.practicum.smarthometech.commerce.payment.application.PaymentService;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
@Validated
public class PaymentController implements PaymentClient {

    private final PaymentService paymentService;

    @Override
    @PostMapping
    public PaymentDto createPayment(@RequestBody OrderDto order) {
        return paymentService.createPayment(order);
    }

    @Override
    @PostMapping("/totalCost")
    public BigDecimal calculateTotalCost(@RequestBody OrderDto orderDto) {
        return paymentService.calculateTotalCost(orderDto);
    }

    @Override
    @PostMapping("/productCost")
    public BigDecimal calculateProductCost(@RequestBody OrderDto orderDto) {
        return paymentService.calculateProductCost(orderDto);
    }

    @Override
    @PostMapping("/successful")
    public void setPaymentSuccessful(@RequestBody UUID paymentId) {
        paymentService.setPaymentSuccessful(paymentId);
    }

    @Override
    @PostMapping("/failed")
    public void setPaymentFailed(@RequestBody UUID paymentId) {
        paymentService.setPaymentFailed(paymentId);
    }
}