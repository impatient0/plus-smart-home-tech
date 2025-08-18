package ru.yandex.practicum.smarthometech.commerce.payment.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.smarthometech.commerce.payment.domain.Payment;
import ru.yandex.practicum.smarthometech.commerce.payment.domain.PaymentRepository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaPaymentRepositoryAdapter implements PaymentRepository {

    private final JpaPaymentRepositoryInterface jpaRepository;

    @Override
    public Optional<Payment> findById(UUID paymentId) {
        return jpaRepository.findById(paymentId);
    }

    @Override
    public Payment save(Payment payment) {
        return jpaRepository.saveAndFlush(payment);
    }
}