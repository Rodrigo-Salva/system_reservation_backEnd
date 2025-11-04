package com.rodrigo.curso.springboot.app.system_reservation_back.adapter.persistence.repository;

import com.rodrigo.curso.springboot.app.system_reservation_back.adapter.persistence.entity.PaymentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * SPRING DATA REPOSITORY para Payment.
 */
@Repository
public interface PaymentJpaRepository extends JpaRepository<PaymentJpaEntity, UUID> {

    /**
     * Buscar pago por ID de reserva.
     */
    Optional<PaymentJpaEntity> findByReservationId(UUID reservationId);

    /**
     * Verificar si existe un pago completado para una reserva.
     */
    boolean existsByReservationIdAndStatus(UUID reservationId, String status);
}
