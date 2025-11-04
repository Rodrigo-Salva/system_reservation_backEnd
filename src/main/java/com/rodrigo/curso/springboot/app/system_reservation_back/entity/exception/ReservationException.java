package com.rodrigo.curso.springboot.app.system_reservation_back.entity.exception;

public class ReservationException extends DomainException {

    public ReservationException(String message) {
        super(message);
    }

    /**
     * RN-015: Prevención de solapamiento
     *
     * Se lanza cuando intentas crear una reserva en un horario
     * que ya está ocupado por otra reserva.
     *
     * Ejemplo:
     * - Reserva 1: 14:00 - 15:00
     * - Reserva 2: 14:30 - 15:30 ← SOLAPA con la primera
     */
    public static ReservationException timeOverlap() {
        return new ReservationException(
                "Time slot not available - overlapping reservation"
        );
    }

    /**
     * RN-016: Anticipación mínima de 24 horas
     *
     * Se lanza cuando intentas crear una reserva con menos de
     * 24 horas de anticipación.
     *
     * Ejemplo:
     * - Hoy es lunes 10:00 AM
     * - Intentas reservar para mañana martes 09:00 AM ← ERROR (solo 23 horas)
     * - Debes reservar para martes 11:00 AM o después (mínimo 24h)
     */
    public static ReservationException insufficientAnticipation() {
        return new ReservationException(
                "Reservation must be at least 24 hours in advance"
        );
    }

    /**
     * RN-017: Dentro de horarios operacionales
     *
     * Se lanza cuando intentas reservar fuera del horario de operación del servicio.
     *
     * Ejemplo:
     * - Servicio opera: Lunes-Viernes 09:00-18:00
     * - Intentas reservar sábado 10:00 ← ERROR (cerrado)
     * - Intentas reservar lunes 20:00 ← ERROR (fuera de horario)
     */
    public static ReservationException outsideOperatingHours() {
        return new ReservationException(
                "Requested time is outside service operating hours"
        );
    }

    /**
     * RN-011: Máximo de reservas simultáneas
     *
     * Se lanza cuando el servicio alcanzó su capacidad máxima.
     *
     * Ejemplo:
     * - Sala de conferencia soporta máx 10 reuniones simultáneas
     * - Ya hay 10 reuniones a las 14:00
     * - Intentas agregar la 11va ← ERROR
     */
    public static ReservationException maxReservationsReached() {
        return new ReservationException(
                "Maximum concurrent reservations reached for this service"
        );
    }

    /**
     * RN-019: Transición de estado inválida
     *
     * Se lanza cuando intentas cambiar el estado de una reserva
     * de manera no permitida.
     *
     * Ejemplo:
     * - Estado actual: COMPLETED (completada)
     * - Intentas cambiar a: CANCELLED ← ERROR (no puedes cancelar una completada)
     */
    public static ReservationException invalidStateTransition(String from, String to) {
        return new ReservationException(
                "Cannot transition from " + from + " to " + to
        );
    }

    /**
     * Se lanza cuando buscas una reserva que no existe.
     */
    public static ReservationException notFound(String id) {
        return new ReservationException("Reservation not found: " + id);
    }

    /**
     * RN-?: No se puede cancelar una reserva completada
     *
     * Ejemplo:
     * - Servicio ya se realizó (estado COMPLETED)
     * - Cliente intenta cancelar ← ERROR (ya pasó)
     */
    public static ReservationException cannotCancelCompleted() {
        return new ReservationException(
                "Cannot cancel a completed reservation"
        );
    }
}