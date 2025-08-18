package ru.yandex.practicum.smarthometech.commerce.api.client;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.smarthometech.commerce.api.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.smarthometech.commerce.api.dto.order.OrderDto;
import ru.yandex.practicum.smarthometech.commerce.api.dto.order.ProductReturnRequest;

@FeignClient(name = "order", path = "/api/v1/order")
public interface OrderClient {

    @GetMapping
    List<OrderDto> getClientOrders(@NotBlank @RequestParam("username") String username);

    @PutMapping
    OrderDto createNewOrder(@Valid @RequestBody CreateNewOrderRequest createNewOrderRequest,
        @NotBlank @RequestParam("username") String username);

    @PostMapping("/return")
    OrderDto productReturn(@Valid @RequestBody ProductReturnRequest productReturnRequest);

    @PostMapping("/payment")
    OrderDto initiatePayment(@NotNull @RequestBody UUID orderId);

    @PostMapping("/payment-successful")
    OrderDto setPaymentSuccessful(@NotNull @RequestBody UUID orderId);

    @PostMapping("/payment-failed")
    OrderDto setPaymentFailed(@NotNull @RequestBody UUID orderId);

    @PostMapping("/delivery")
    OrderDto initiateDelivery(@NotNull @RequestBody UUID orderId);

    @PostMapping("/delivery-successful")
    OrderDto setDeliverySuccessful(@NotNull @RequestBody UUID orderId);

    @PostMapping("/delivery-failed")
    OrderDto setDeliveryFailed(@NotNull @RequestBody UUID orderId);

    @PostMapping("/completed")
    OrderDto markAsCompleted(@NotNull @RequestBody UUID orderId);

    @PostMapping("/calculate/total")
    OrderDto calculateTotalCost(@NotNull @RequestBody UUID orderId);

    @PostMapping("/calculate/delivery")
    OrderDto calculateDeliveryCost(@NotNull @RequestBody UUID orderId);

    @PostMapping("/assembly")
    OrderDto startAssembly(@NotNull @RequestBody UUID orderId);

    @PostMapping("/assembly-failed")
    OrderDto setAssemblyFailed(@NotNull @RequestBody UUID orderId);
}