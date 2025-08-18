package ru.yandex.practicum.smarthometech.commerce.api.client;

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
    List<OrderDto> getClientOrders(@RequestParam("username") String username);

    @PutMapping
    OrderDto createNewOrder(@RequestBody CreateNewOrderRequest createNewOrderRequest,
        @RequestParam("username") String username);

    @PostMapping("/return")
    OrderDto productReturn(@RequestBody ProductReturnRequest productReturnRequest);

    @PostMapping("/payment")
    OrderDto initiatePayment(@RequestBody UUID orderId);

    @PostMapping("/payment/successful")
    OrderDto setPaymentSuccessful(@RequestBody UUID orderId);

    @PostMapping("/payment/failed")
    OrderDto setPaymentFailed(@RequestBody UUID orderId);

    @PostMapping("/delivery")
    OrderDto setAsDelivered(@RequestBody UUID orderId);

    @PostMapping("/delivery/successful")
    OrderDto setDeliverySuccessful(@RequestBody UUID orderId);

    @PostMapping("/delivery/failed")
    OrderDto setDeliveryFailed(@RequestBody UUID orderId);

    @PostMapping("/completed")
    OrderDto markAsCompleted(@RequestBody UUID orderId);

    @PostMapping("/calculate/total")
    OrderDto calculateTotalCost(@RequestBody UUID orderId);

    @PostMapping("/calculate/delivery")
    OrderDto calculateDeliveryCost(@RequestBody UUID orderId);

    @PostMapping("/assembly")
    OrderDto startAssembly(@RequestBody UUID orderId);

    @PostMapping("/assembly/failed")
    OrderDto setAssemblyFailed(@RequestBody UUID orderId);
}