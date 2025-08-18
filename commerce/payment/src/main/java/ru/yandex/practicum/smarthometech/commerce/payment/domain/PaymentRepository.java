package ru.yandex.practicum.smarthometech.commerce.payment.domain;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository {

    Optional<Payment> findById(UUID paymentId);

    Payment save(Payment payment);

}