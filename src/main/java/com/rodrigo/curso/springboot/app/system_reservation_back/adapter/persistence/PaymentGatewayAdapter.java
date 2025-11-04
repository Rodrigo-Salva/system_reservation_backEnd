package com.rodrigo.curso.springboot.app.system_reservation_back.adapter.persistence;

import com.rodrigo.curso.springboot.app.system_reservation_back.adapter.persistence.entity.PaymentJpaEntity;
import com.rodrigo.curso.springboot.app.system_reservation_back.adapter.persistence.repository.PaymentJpaRepository;
import com.rodrigo.curso.springboot.app.system_reservation_back.common.mapper.PaymentMapper;
import com.rodrigo.curso.springboot.app.system_reservation_back.entity.Payment;
import com.rodrigo.curso.springboot.app.system_reservation_back.usecase.gateway.PaymentGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * ADAPTER: Implementación de PaymentGateway usando JPA.
 */
@Repository
@RequiredArgsConstructor
public class PaymentGatewayAdapter implements PaymentGateway {

    private final PaymentJpaRepository paymentJpaRepository;
    private final PaymentMapper paymentMapper;

    @Override
    public Payment save(Payment payment) {
        PaymentJpaEntity jpaEntity = paymentMapper.toEntity(payment);
        PaymentJpaEntity savedEntity = paymentJpaRepository.save(jpaEntity);
        return paymentMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Payment> findById(UUID id) {
        return paymentJpaRepository.findById(id)
                .map(paymentMapper::toDomain);
    }

    @Override
    public Optional<Payment> findByReservationId(UUID reservationId) {
        return paymentJpaRepository.findByReservationId(reservationId)
                .map(paymentMapper::toDomain);
    }

    @Override
    public boolean hasCompletedPayment(UUID reservationId) {
        return paymentJpaRepository.existsByReservationIdAndStatus(
                reservationId,
                "COMPLETED"
        );
    }
}
