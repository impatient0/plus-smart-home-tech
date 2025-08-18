package ru.yandex.practicum.smarthometech.commerce.order.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.smarthometech.commerce.api.client.OrderClient;
import ru.yandex.practicum.smarthometech.commerce.api.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.smarthometech.commerce.api.dto.order.OrderDto;
import ru.yandex.practicum.smarthometech.commerce.api.dto.order.ProductReturnRequest;
import ru.yandex.practicum.smarthometech.commerce.order.application.OrderService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController implements OrderClient {

    private final OrderService orderService;

    // --- USER-FACING ENDPOINTS ---

    @Override
    @PutMapping
    @ResponseStatus(HttpStatus.CREATED) // Use 201 CREATED for new resource creation
    public OrderDto createNewOrder(@RequestBody CreateNewOrderRequest createNewOrderRequest, @RequestParam("username") String username) {
        return orderService.createNewOrder(createNewOrderRequest, username);
    }

    @Override
    @GetMapping
    public List<OrderDto> getClientOrders(@RequestParam("username") String username) {
        return orderService.getClientOrders(username);
    }

    @Override
    @PostMapping("/return")
    public OrderDto productReturn(@RequestBody ProductReturnRequest productReturnRequest) {
        return orderService.productReturn(productReturnRequest);
    }

    // --- INTERNAL CALLBACK ENDPOINTS ---

    @Override
    @PostMapping("/{orderId}/payment-successful")
    public OrderDto setPaymentSuccessful(@PathVariable("orderId") UUID orderId) {
        return orderService.setPaymentSuccessful(orderId);
    }

    @Override
    @PostMapping("/{orderId}/payment-failed")
    public OrderDto setPaymentFailed(@PathVariable("orderId") UUID orderId) {
        return orderService.setPaymentFailed(orderId);
    }

    @Override
    @PostMapping("/{orderId}/assembly-failed")
    public OrderDto setAssemblyFailed(@PathVariable("orderId") UUID orderId) {
        return orderService.setAssemblyFailed(orderId);
    }

    @Override
    @PostMapping("/{orderId}/delivery-successful")
    public OrderDto setDeliverySuccessful(@PathVariable("orderId") UUID orderId) {
        return orderService.setDeliverySuccessful(orderId);
    }

    @Override
    @PostMapping("/{orderId}/delivery-failed")
    public OrderDto setDeliveryFailed(@PathVariable("orderId") UUID orderId) {
        return orderService.setDeliveryFailed(orderId);
    }


    // --- REDUNDANT/NOT IMPLEMENTED ENDPOINTS ---

    @Override
    @PostMapping("/payment")
    public OrderDto initiatePayment(@RequestBody UUID orderId) {
        throw new UnsupportedOperationException("Payment is initiated as part of the order creation process. This endpoint is not supported.");
    }

    @Override
    @PostMapping("/assembly")
    public OrderDto startAssembly(@RequestBody UUID orderId) {
        throw new UnsupportedOperationException("Assembly is initiated as part of the order creation process. This endpoint is not supported.");
    }

    @Override
    @PostMapping("/completed")
    public OrderDto markAsCompleted(@RequestBody UUID orderId) {
        throw new UnsupportedOperationException("Order is completed automatically after successful delivery. This endpoint is not supported.");
    }

    @Override
    @PostMapping("/delivery")
    public OrderDto initiateDelivery(@RequestBody UUID orderId) {
        throw new UnsupportedOperationException("Delivery is initiated automatically after payment has been completed. This endpoint is not supported.");
    }

    @Override
    @PostMapping
    public OrderDto calculateTotalCost(@RequestBody UUID orderId) {
        throw new UnsupportedOperationException("Total cost is calculated during the order creation process.");
    }

    @Override
    @PostMapping
    public OrderDto calculateDeliveryCost(@RequestBody UUID orderId) {
        throw new UnsupportedOperationException("Delivery cost is calculated during the order creation process.");
    }

}