package com.rodrigo.curso.springboot.app.system_reservation_back.usecase.dto;


import com.rodrigo.curso.springboot.app.system_reservation_back.entity.Payment;
import com.rodrigo.curso.springboot.app.system_reservation_back.entity.Reservation;
import com.rodrigo.curso.springboot.app.system_reservation_back.entity.exception.ReservationException;
import com.rodrigo.curso.springboot.app.system_reservation_back.usecase.dto.request.CancelReservationRequest;
import com.rodrigo.curso.springboot.app.system_reservation_back.usecase.dto.response.ReservationResponse;
import com.rodrigo.curso.springboot.app.system_reservation_back.usecase.gateway.PaymentGateway;
import com.rodrigo.curso.springboot.app.system_reservation_back.usecase.gateway.ReservationGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * USE CASE: Cancelar una reserva.
 *
 * RESPONSABILIDADES:
 * 1. Validar que la reserva existe
 * 2. RN-019: Validar que se puede cancelar (no sea COMPLETED)
 * 3. RN-025: Validar que el usuario es dueño de la reserva
 * 4. Cambiar estado a CANCELLED
 * 5. Si tiene pago completado, procesar reembolso
 * 6. RN-006: Calcular monto de reembolso según política
 * 7. Persistir cambios
 * 8. Retornar DTO
 *
 * ¿Por qué es complejo?
 * Porque implica dos entidades (Reservation + Payment)
 * y debe ser ATÓMICO (todo o nada).
 */
@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class CancelReservationUseCase {

    // DEPENDENCIAS
    private final ReservationGateway reservationGateway;
    private final PaymentGateway paymentGateway;

    /**
     * EJECUTA el caso de uso.
     *
     * @param reservationId ID de la reserva a cancelar
     * @param userId ID del usuario (para validar ownership)
     * @param request DTO con razón de cancelación
     * @return DTO con la reserva cancelada
     * @throws ReservationException si no se puede cancelar
     */
    @Transactional  // TODO en una transacción
    public ReservationResponse execute(
            UUID reservationId,
            UUID userId,
            CancelReservationRequest request
    ) {

        // PASO 1: Buscar la reserva
        Reservation reservation = reservationGateway.findById(reservationId)
                .orElseThrow(() -> ReservationException.notFound(reservationId.toString()));

        // PASO 2: RN-025 - Validar que el usuario es dueño
        if (!reservation.getUserId().equals(userId)) {
            throw new ReservationException(
                    "You can only cancel your own reservations"
            );
        }

        // PASO 3: RN-019 - Validar que se pueda cancelar
        // Esto lanzará excepción si está COMPLETED
        // (el método cancel() del entity hace esta validación)

        // PASO 4: Cancelar la reserva (cambia estado)
        reservation.cancel();  // PENDING/CONFIRMED → CANCELLED

        // PASO 5: Si hay un pago completado, reembolsar
        Payment payment = paymentGateway.findByReservationId(reservationId)
                .orElse(null);

        if (payment != null && payment.isCompleted()) {
            // RN-006: Calcular monto de reembolso según política
            RefundPolicy refundPolicy = calculateRefundPolicy(reservation);

            // Marcar pago como reembolsado
            payment.refund();
            paymentGateway.save(payment);

            // Aquí en producción, llamarías al proveedor de pago
            // para procesar el reembolso real (Stripe, PayPal, etc)
            // processPaymentProviderRefund(payment, refundPolicy.getRefundAmount());
        }

        // PASO 6: Persistir cambios
        Reservation cancelledReservation = reservationGateway.save(reservation);

        // PASO 7: Retornar DTO
        return mapToResponse(cancelledReservation);
    }

    /**
     * RN-006: CALCULA POLÍTICA DE REEMBOLSO
     *
     * Reglas:
     * - 48+ horas antes: 100% reembolso
     * - 24-48 horas: 50% reembolso
     * - < 24 horas: 0% reembolso
     *
     * @return Objeto con monto y porcentaje de reembolso
     */
    private RefundPolicy calculateRefundPolicy(Reservation reservation) {
        long hoursUntilReservation = java.time.temporal.ChronoUnit.HOURS
                .between(
                        java.time.LocalDateTime.now(),
                        reservation.getDateRange().getStartDateTime()
                );

        if (hoursUntilReservation >= 48) {
            // 100% reembolso
            return new RefundPolicy(100.0, "Full refund - cancelled 48+ hours before");
        } else if (hoursUntilReservation >= 24) {
            // 50% reembolso
            return new RefundPolicy(50.0, "Partial refund (50%) - cancelled 24-48 hours before");
        } else {
            // 0% reembolso
            return new RefundPolicy(0.0, "No refund - cancelled less than 24 hours before");
        }
    }

    /**
     * MAPPER: Convierte Entity a DTO
     */
    private ReservationResponse mapToResponse(Reservation reservation) {
        return ReservationResponse.builder()
                .id(reservation.getId())
                .userId(reservation.getUserId())
                .serviceId(reservation.getServiceId())
                .date(reservation.getDateRange().getDate())
                .startTime(reservation.getDateRange().getStartTime())
                .endTime(reservation.getDateRange().getEndTime())
                .status(reservation.getStatus().name())
                .notes(reservation.getNotes())
                .createdAt(reservation.getCreatedAt())
                .build();
    }

    /**
     * INNER CLASS: Política de reembolso
     */
    private static class RefundPolicy {
        private final Double refundPercentage;
        private final String reason;

        RefundPolicy(Double refundPercentage, String reason) {
            this.refundPercentage = refundPercentage;
            this.reason = reason;
        }

        public Double getRefundPercentage() {
            return refundPercentage;
        }

        public String getReason() {
            return reason;
        }
    }
}
