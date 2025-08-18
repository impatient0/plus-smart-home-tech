package ru.yandex.practicum.smarthometech.commerce.order.application;

import java.util.HashMap;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.smarthometech.commerce.api.client.DeliveryClient;
import ru.yandex.practicum.smarthometech.commerce.api.client.PaymentClient;
import ru.yandex.practicum.smarthometech.commerce.api.client.WarehouseClient;
import ru.yandex.practicum.smarthometech.commerce.api.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.smarthometech.commerce.api.dto.delivery.DeliveryDto;
import ru.yandex.practicum.smarthometech.commerce.api.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.smarthometech.commerce.api.dto.order.OrderDto;
import ru.yandex.practicum.smarthometech.commerce.api.dto.order.ProductReturnRequest;
import ru.yandex.practicum.smarthometech.commerce.api.dto.warehouse.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.smarthometech.commerce.api.exception.DeliveryClientException;
import ru.yandex.practicum.smarthometech.commerce.api.exception.InsufficientQuantityException;
import ru.yandex.practicum.smarthometech.commerce.api.exception.OrderCreationException;
import ru.yandex.practicum.smarthometech.commerce.api.exception.OrderNotFoundException;
import ru.yandex.practicum.smarthometech.commerce.api.exception.PaymentClientException;
import ru.yandex.practicum.smarthometech.commerce.api.exception.ProductNotFoundException;
import ru.yandex.practicum.smarthometech.commerce.api.mapper.CartApiMapper;
import ru.yandex.practicum.smarthometech.commerce.api.mapper.AddressApiMapper;
import ru.yandex.practicum.smarthometech.commerce.order.domain.Order;
import ru.yandex.practicum.smarthometech.commerce.order.domain.OrderItem;
import ru.yandex.practicum.smarthometech.commerce.order.domain.OrderRepository;
import ru.yandex.practicum.smarthometech.commerce.order.domain.OrderStatus;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final CartApiMapper cartApiMapper;
    private final AddressApiMapper addressMapper;
    private final WarehouseClient warehouseClient;
    private final DeliveryClient deliveryClient;
    private final PaymentClient paymentClient;

    @Transactional
    public OrderDto createNewOrder(CreateNewOrderRequest request, String username) {
        ShoppingCartDto cart = cartApiMapper.toCartDto(request.getShoppingCart());
        log.info("Creating new order for user '{}' from cart '{}'", username, cart.getShoppingCartId());

        // Step 1: Create the initial Order entity in a 'NEW' state.
        Order order = new Order();
        order.setUsername(username);
        order.setShoppingCartId(cart.getShoppingCartId());
        order.setStatus(OrderStatus.NEW);

        order.setDeliveryAddressStreet(request.getDeliveryAddress().getStreet());

        for (Map.Entry<String, Long> entry : cart.getProducts().entrySet()) {
            OrderItem item = new OrderItem();
            item.setProductId(UUID.fromString(entry.getKey()));
            item.setQuantity(entry.getValue());
            order.addItem(item);
        }

        // Save the initial order to get its generated ID
        Order savedOrder = orderRepository.save(order);
        UUID orderId = savedOrder.getOrderId();
        log.info("Order {} created with status NEW.", orderId);

        // Step 2: Attempt to assemble and reserve items in warehouse
        try {
            var assemblyRequest = new AssemblyProductsForOrderRequest()
                .orderId(orderId)
                .products(cart.getProducts());
            var bookedProducts = warehouseClient.assembleOrder(assemblyRequest);
            log.info("Warehouse confirmed assembly for order: {}", orderId);

            // Step 3: Plan the delivery
            var warehouseAddress = addressMapper.toDeliveryDto(warehouseClient.getWarehouseAddress());
            var deliveryRequest = new DeliveryDto()
                .orderId(orderId)
                .fromAddress(warehouseAddress)
                .toAddress(addressMapper.toDeliveryDto(request.getDeliveryAddress()));
            var plannedDelivery = deliveryClient.planDelivery(deliveryRequest);
            savedOrder.setDeliveryId(plannedDelivery.getDeliveryId());
            log.info("Delivery {} planned for order {}.", plannedDelivery.getDeliveryId(), orderId);

            // Step 4: Create the payment record
            OrderDto orderDtoForPayment = orderMapper.toDto(savedOrder);
            orderDtoForPayment.setDeliveryWeight(bookedProducts.getDeliveryWeight());
            orderDtoForPayment.setDeliveryVolume(bookedProducts.getDeliveryVolume());
            orderDtoForPayment.setFragile(bookedProducts.getFragile());
            orderDtoForPayment.setDeliveryPrice(deliveryClient.getDeliveryCost(orderDtoForPayment));

            var paymentDto = paymentClient.createPayment(orderDtoForPayment);
            savedOrder.setPaymentId(paymentDto.getPaymentId());
            log.info("Payment {} created for order {}.", paymentDto.getPaymentId(), orderId);

            // Step 5: Finalize the order with all IDs and calculated prices, and set status to ON_PAYMENT
            savedOrder.setFee(paymentDto.getFeeTotal());
            savedOrder.setDeliveryPrice(paymentDto.getDeliveryTotal());
            savedOrder.setTotalPrice(paymentDto.getTotalPayment());
            savedOrder.setStatus(OrderStatus.ON_PAYMENT);
            log.info("Order {} is now ON_PAYMENT.", orderId);

        } catch (ProductNotFoundException | InsufficientQuantityException e) {
            // Assembly failure
            log.warn("Failed to create order for cartId: {} due to warehouse issues: {}", cart.getShoppingCartId(), e.getMessage());
            savedOrder.setStatus(OrderStatus.ASSEMBLY_FAILED);
        } catch (Exception e) {
            // Unexpected error (e.g. Warehouse is down)
            log.error("An unexpected error occurred during order creation for initial orderId {}", orderId, e);
            throw new OrderCreationException(cart.getShoppingCartId(), e.getMessage());
        }

        Order finalOrder = orderRepository.save(savedOrder);

        return orderMapper.toDto(finalOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderDto> getClientOrders(String username) {
        return orderRepository.findByUsername(username).stream()
            .map(orderMapper::toDto)
            .collect(Collectors.toList());
    }

    @Transactional
    public OrderDto productReturn(ProductReturnRequest request) {
        log.debug("Processing product return for orderId: {}", request.getOrderId());
        Order order = findOrderById(request.getOrderId());
        Map<String, Long> warehouseRequest = new HashMap<>();
        for (Map.Entry<String, Long> entry : request.getProducts().entrySet()) {
            UUID productId = UUID.fromString(entry.getKey());
            Long quantityToReturn = entry.getValue();
            Optional<OrderItem> orderItem = order.getItems().stream().filter(item -> item.getProductId().equals(productId)).findFirst();
            if (orderItem.isEmpty()) {
                log.warn("Product {} not found in order {}. Skipping return for this item.", productId, order.getOrderId());
                continue;
            }
            Long quantityInOrder = orderItem.get().getQuantity();
            if (quantityToReturn > quantityInOrder) {
                log.warn("Requested return quantity {} for product {} exceeds quantity in order {}. Returning available quantity {}.",
                    quantityToReturn, productId, order.getOrderId(), quantityInOrder);
                quantityToReturn = quantityInOrder;
                order.getItems().remove(orderItem.get());
            } else {
                orderItem.get().setQuantity(quantityInOrder - quantityToReturn);
            }
            warehouseRequest.put(productId.toString(), quantityToReturn);
        }
        Order savedOrder = orderRepository.save(order);
        warehouseClient.acceptProductReturn(warehouseRequest);
        log.info("Product return processed for order {}.", order.getOrderId());
        return orderMapper.toDto(savedOrder);
    }

    // --- callbacks ---

    @Transactional
    public OrderDto setPaymentSuccessful(UUID orderId) {
        Order order = findOrderById(orderId);
        if (order.getStatus() != OrderStatus.ON_PAYMENT) {
            log.warn("Order {} was already processed. Current status: {}", orderId, order.getStatus());
            return orderMapper.toDto(order);
        }
        order.setStatus(OrderStatus.PAID);
        log.info("Order {} status updated to PAID.", orderId);
        // initiate delivery? since there's a separate endpoint for that, assuming it is being done manually
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Transactional
    public OrderDto setPaymentFailed(UUID orderId) {
        Order order = findOrderById(orderId);
        order.setStatus(OrderStatus.PAYMENT_FAILED);
        log.info("Order {} status updated to PAYMENT_FAILED.", orderId);
        // release stock in the warehouse? or wait for customer to retry payment?
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Transactional
    public OrderDto setDeliverySuccessful(UUID orderId) {
        Order order = findOrderById(orderId);
        if (order.getStatus() != OrderStatus.ON_DELIVERY) {
            log.warn("Order {} was already processed. Current status: {}", orderId, order.getStatus());
            return orderMapper.toDto(order);
        }
        order.setStatus(OrderStatus.DELIVERED);
        log.info("Order {} status updated to DELIVERED.", orderId);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Transactional
    public OrderDto setDeliveryFailed(UUID orderId) {
        Order order = findOrderById(orderId);
        order.setStatus(OrderStatus.DELIVERY_FAILED);
        log.info("Order {} status updated to DELIVERY_FAILED.", orderId);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Transactional
    public OrderDto setAssemblyFailed(UUID orderId) {
        Order order = findOrderById(orderId);
        order.setStatus(OrderStatus.ASSEMBLY_FAILED);
        log.info("Order {} status updated to ASSEMBLY_FAILED.", orderId);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Transactional
    public OrderDto markAsCompleted(UUID orderId) {
        Order order = findOrderById(orderId);
        if (order.getStatus() != OrderStatus.DELIVERED) {
            log.warn("Order {} cannot be completed. Current status: {}", orderId, order.getStatus());
            return orderMapper.toDto(order);
        }
        order.setStatus(OrderStatus.COMPLETED);
        log.info("Order {} status updated to COMPLETED.", orderId);
        return orderMapper.toDto(orderRepository.save(order));
    }

    private Order findOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException(orderId));
    }
}