package ru.yandex.practicum.smarthometech.commerce.delivery.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.smarthometech.commerce.api.client.OrderClient;
import ru.yandex.practicum.smarthometech.commerce.api.client.WarehouseClient;
import ru.yandex.practicum.smarthometech.commerce.api.dto.delivery.DeliveryDto;
import ru.yandex.practicum.smarthometech.commerce.api.dto.order.OrderDto;
import ru.yandex.practicum.smarthometech.commerce.api.dto.warehouse.ShippedToDeliveryRequest;
import ru.yandex.practicum.smarthometech.commerce.api.exception.DeliveryNotFoundException;
import ru.yandex.practicum.smarthometech.commerce.delivery.domain.Delivery;
import ru.yandex.practicum.smarthometech.commerce.delivery.domain.DeliveryRepository;
import ru.yandex.practicum.smarthometech.commerce.delivery.domain.DeliveryStatus;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryMapper deliveryMapper;
    private final OrderClient orderClient;
    private final WarehouseClient warehouseClient;

    @Transactional
    public DeliveryDto planDelivery(DeliveryDto deliveryDto) {
        log.info("Planning new delivery for orderId: {}", deliveryDto.getOrderId());
        Delivery delivery = deliveryMapper.toEntity(deliveryDto);
        delivery.setStatus(DeliveryStatus.CREATED);

        Delivery savedDelivery = deliveryRepository.save(delivery);
        log.info("Successfully planned delivery with id: {}", savedDelivery.getDeliveryId());

        return deliveryMapper.toDto(savedDelivery);
    }

    @Transactional(readOnly = true)
    public BigDecimal calculateDeliveryCost(OrderDto orderDto) {
        log.debug("Calculating delivery cost for orderId: {}", orderDto.getOrderId());

        Delivery delivery = deliveryRepository.findByOrderId(orderDto.getOrderId())
            .orElseThrow(() -> new DeliveryNotFoundException(orderDto.getOrderId()));

        BigDecimal baseCost = new BigDecimal("5.0");
        BigDecimal cost = new BigDecimal("5.0");

        String fromStreet = delivery.getFromStreet();
        if (fromStreet != null && fromStreet.contains("ADDRESS_2")) {
            cost = cost.add(baseCost.multiply(BigDecimal.valueOf(2)));
        } else {
            cost = cost.add(baseCost.multiply(BigDecimal.valueOf(1)));
        }

        if (Boolean.TRUE.equals(orderDto.getFragile())) {
            cost = cost.add(cost.multiply(new BigDecimal("0.2")));
        }

        BigDecimal deliveryWeight = (orderDto.getDeliveryWeight() != null)
            ? BigDecimal.valueOf(orderDto.getDeliveryWeight())
            : BigDecimal.ZERO;

        BigDecimal deliveryVolume = (orderDto.getDeliveryVolume() != null)
            ? BigDecimal.valueOf(orderDto.getDeliveryVolume())
            : BigDecimal.ZERO;

        cost = cost.add(deliveryWeight.multiply(new BigDecimal("0.3")));
        cost = cost.add(deliveryVolume.multiply(new BigDecimal("0.2")));

        String toStreet = delivery.getToStreet();
        if (fromStreet != null && toStreet != null && !fromStreet.equals(toStreet)) {
            cost = cost.add(cost.multiply(new BigDecimal("0.2")));
        }

        log.info("Calculated delivery cost for orderId {}: {}", orderDto.getOrderId(), cost);
        return cost.setScale(2, java.math.RoundingMode.HALF_UP);
    }

    @Transactional
    public void setDeliveryPickedUp(UUID deliveryId) {
        log.info("Setting delivery {} to IN_PROGRESS", deliveryId);
        Delivery delivery = findDeliveryById(deliveryId);
        delivery.setStatus(DeliveryStatus.IN_PROGRESS);
        deliveryRepository.save(delivery);

        orderClient.setAssemblySuccessful(delivery.getOrderId());
        warehouseClient.markAsShippedToDelivery(
            new ShippedToDeliveryRequest().orderId(delivery.getOrderId()).deliveryId(deliveryId)
        );
        log.info("Delivery {} is now IN_PROGRESS. Notified other services.", deliveryId);
    }

    @Transactional
    public void setDeliverySuccessful(UUID deliveryId) {
        log.info("Setting delivery {} to DELIVERED", deliveryId);
        Delivery delivery = findDeliveryById(deliveryId);
        delivery.setStatus(DeliveryStatus.DELIVERED);
        deliveryRepository.save(delivery);

        orderClient.setDeliverySuccessful(delivery.getOrderId());
        log.info("Delivery {} is now DELIVERED. Notified order service.", deliveryId);
    }

    @Transactional
    public void setDeliveryFailed(UUID deliveryId) {
        log.info("Setting delivery {} to FAILED", deliveryId);
        Delivery delivery = findDeliveryById(deliveryId);
        delivery.setStatus(DeliveryStatus.FAILED);
        deliveryRepository.save(delivery);

        orderClient.setDeliveryFailed(delivery.getOrderId());
        log.info("Delivery {} is now FAILED. Notified order service.", deliveryId);
    }

    private Delivery findDeliveryById(UUID deliveryId) {
        return deliveryRepository.findById(deliveryId)
            .orElseThrow(() -> new DeliveryNotFoundException(deliveryId));
    }
}