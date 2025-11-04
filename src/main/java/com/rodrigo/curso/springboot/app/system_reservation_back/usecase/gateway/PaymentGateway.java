package com.rodrigo.curso.springboot.app.system_reservation_back.usecase.gateway;

import com.rodrigo.curso.springboot.app.system_reservation_back.entity.Payment;

import java.util.Optional;
import java.util.UUID;

/**
 * GATEWAY para acceder a pagos.
 */
public interface PaymentGateway {

    /**
     * Guarda o actualiza un pago.
     */
    Payment save(Payment payment);

    /**
     * Busca un pago por ID.
     */
    Optional<Payment> findById(UUID id);

    /**
     * Busca un pago por ID de reserva.
     */
    Optional<Payment> findByReservationId(UUID reservationId);

    /**
     * Verifica si una reserva tiene un pago completado.
     */
    boolean hasCompletedPayment(UUID reservationId);
}
