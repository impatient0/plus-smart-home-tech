package ru.yandex.practicum.smarthometech.commerce.payment.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.smarthometech.commerce.payment.domain.Payment;

import java.util.UUID;

public interface JpaPaymentRepositoryInterface extends JpaRepository<Payment, UUID> {
}