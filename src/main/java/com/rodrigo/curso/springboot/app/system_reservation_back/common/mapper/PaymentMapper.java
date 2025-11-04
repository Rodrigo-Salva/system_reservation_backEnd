package com.rodrigo.curso.springboot.app.system_reservation_back.common.mapper;

import com.rodrigo.curso.springboot.app.system_reservation_back.adapter.persistence.entity.PaymentJpaEntity;
import com.rodrigo.curso.springboot.app.system_reservation_back.entity.Payment;
import com.rodrigo.curso.springboot.app.system_reservation_back.entity.valuobject.Money;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * MAPPER: Convierte entre Payment (Dominio) y PaymentJpaEntity (JPA)
 */
@Component
public class PaymentMapper {

    /**
     * CONVERTIR: PaymentJpaEntity → Payment (Dominio)
     */
    public Payment toDomain(PaymentJpaEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }

        return Payment.reconstructFromDatabase(
                jpaEntity.getId(),
                jpaEntity.getReservationId(),
                new Money(
                        jpaEntity.getAmount(),
                        jpaEntity.getCurrency()
                ),
                jpaEntity.getPaymentMethod(),
                Payment.PaymentStatus.valueOf(jpaEntity.getStatus().toString()),
                jpaEntity.getTransactionId(),
                jpaEntity.getErrorMessage(),
                jpaEntity.getProcessedAt(),
                jpaEntity.getCreatedAt()
        );
    }

    /**
     * CONVERTIR: Payment (Dominio) → PaymentJpaEntity
     */
    public PaymentJpaEntity toEntity(Payment domain) {
        if (domain == null) {
            return null;
        }

        return PaymentJpaEntity.builder()
                .id(domain.getId())
                .reservationId(domain.getReservationId())
                .amount(domain.getAmount().getAmount())
                .currency(domain.getAmount().getCurrency())
                .paymentMethod(domain.getPaymentMethod())
                .status(domain.getStatus().toString())
                .transactionId(domain.getTransactionId())
                .errorMessage(domain.getErrorMessage())
                .processedAt(domain.getProcessedAt())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}

