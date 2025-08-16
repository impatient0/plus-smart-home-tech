package ru.yandex.practicum.smarthometech.commerce.payment.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.smarthometech.commerce.api.client.OrderClient;
import ru.yandex.practicum.smarthometech.commerce.api.client.ShoppingStoreClient;
import ru.yandex.practicum.smarthometech.commerce.api.dto.order.OrderDto;
import ru.yandex.practicum.smarthometech.commerce.api.dto.payment.PaymentDto;
import ru.yandex.practicum.smarthometech.commerce.api.exception.PaymentNotFoundException;
import ru.yandex.practicum.smarthometech.commerce.payment.domain.Payment;
import ru.yandex.practicum.smarthometech.commerce.payment.domain.PaymentStatus;
import ru.yandex.practicum.smarthometech.commerce.payment.domain.PaymentRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private static final BigDecimal TAX_RATE = new BigDecimal("0.10");

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final ShoppingStoreClient shoppingStoreClient;
    private final OrderClient orderClient;

    @Transactional
    public PaymentDto createPayment(OrderDto orderDto) {
        log.info("Creating new payment record for orderId: {}", orderDto.getOrderId());

        BigDecimal productCost = calculateProductCost(orderDto);
        BigDecimal deliveryCost = orderDto.getDeliveryPrice() == null ? BigDecimal.ZERO : orderDto.getDeliveryPrice();
        BigDecimal tax = productCost.multiply(TAX_RATE);
        BigDecimal totalPayment = productCost.add(tax).add(deliveryCost);

        Payment newPayment = new Payment();
        newPayment.setPaymentId(UUID.randomUUID());
        newPayment.setOrderId(orderDto.getOrderId());
        newPayment.setProductCost(productCost.setScale(2, RoundingMode.HALF_UP));
        newPayment.setDeliveryCost(deliveryCost.setScale(2, RoundingMode.HALF_UP));
        newPayment.setTaxAmount(tax.setScale(2, RoundingMode.HALF_UP));
        newPayment.setTotalPayment(totalPayment.setScale(2, RoundingMode.HALF_UP));
        newPayment.setStatus(PaymentStatus.PENDING);

        Payment savedPayment = paymentRepository.save(newPayment);
        log.info("Payment record {} created for order {}", savedPayment.getPaymentId(), savedPayment.getOrderId());

        return paymentMapper.toDto(savedPayment);
    }

    @Transactional(readOnly = true)
    public BigDecimal calculateProductCost(OrderDto orderDto) {
        log.debug("Calculating product cost for orderId: {}", orderDto.getOrderId());

        BigDecimal totalProductCost = BigDecimal.ZERO;
        for (Map.Entry<String, Long> entry : orderDto.getProducts().entrySet()) {
            UUID productId = UUID.fromString(entry.getKey());
            long quantity = entry.getValue();

            BigDecimal price = shoppingStoreClient.getProduct(productId).getPrice();
            totalProductCost = totalProductCost.add(price.multiply(BigDecimal.valueOf(quantity)));
        }

        return totalProductCost.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateTotalCost(OrderDto orderDto) {
        log.debug("Calculating total cost for orderId: {}", orderDto.getOrderId());

        BigDecimal productCost = calculateProductCost(orderDto);
        BigDecimal tax = productCost.multiply(TAX_RATE);
        BigDecimal deliveryCost = orderDto.getDeliveryPrice();

        return productCost.add(tax).add(deliveryCost).setScale(2, RoundingMode.HALF_UP);
    }

    @Transactional
    public void setPaymentSuccessful(UUID paymentId) {
        log.info("Processing successful payment for paymentId: {}", paymentId);
        Payment payment = findPaymentById(paymentId);

        if (payment.getStatus() != PaymentStatus.PENDING) {
            log.warn("Payment {} was already processed. Current status: {}", paymentId, payment.getStatus());
            return;
        }

        payment.setStatus(PaymentStatus.SUCCESS);
        paymentRepository.save(payment);

        orderClient.setPaymentSuccessful(payment.getOrderId());
        log.info("Payment {} marked as SUCCESS. Notified order service for order {}.", paymentId, payment.getOrderId());
    }

    @Transactional
    public void setPaymentFailed(UUID paymentId) {
        log.info("Processing failed payment for paymentId: {}", paymentId);
        Payment payment = findPaymentById(paymentId);

        if (payment.getStatus() != PaymentStatus.PENDING) {
            log.warn("Payment {} was already processed. Current status: {}", paymentId, payment.getStatus());
            return;
        }

        payment.setStatus(PaymentStatus.FAILED);
        paymentRepository.save(payment);

        orderClient.setPaymentFailed(payment.getOrderId());
        log.info("Payment {} marked as FAILED. Notified order service for order {}.", paymentId, payment.getOrderId());
    }

    private Payment findPaymentById(UUID paymentId) {
        return paymentRepository.findById(paymentId)
            .orElseThrow(() -> new PaymentNotFoundException(paymentId));
    }
}