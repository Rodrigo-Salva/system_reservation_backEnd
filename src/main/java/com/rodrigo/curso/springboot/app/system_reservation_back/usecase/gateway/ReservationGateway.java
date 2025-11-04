package com.rodrigo.curso.springboot.app.system_reservation_back.usecase.gateway;

import com.rodrigo.curso.springboot.app.system_reservation_back.entity.Reservation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * GATEWAY para acceder a reservas.
 *
 * Este es el gateway MÁS IMPORTANTE porque
 * necesita métodos complejos para validar solapamiento.
 */
public interface ReservationGateway {

    /**
     * Guarda o actualiza una reserva.
     */
    Reservation save(Reservation reservation);

    /**
     * Busca una reserva por ID.
     */
    Optional<Reservation> findById(UUID id);

    /**
     * RN-015: BUSCA RESERVAS CONFIRMADAS O COMPLETADAS
     * del mismo servicio en una fecha/hora específica.
     *
     * Esto es CRÍTICO para detectar solapamiento.
     *
     * @param serviceId Servicio a buscar
     * @param date Fecha de la reserva
     * @param startTime Hora de inicio
     * @param endTime Hora de fin
     * @return Lista de reservas que podrían solapar
     */
    List<Reservation> findOverlappingReservations(
            UUID serviceId,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime
    );

    /**
     * RN-021: Cuenta reservas CONFIRMED activas de un usuario.
     *
     * Para validar que no tenga más de 5 activas.
     */
    long countActiveReservationsByUser(UUID userId);

    /**
     * RN-022: Cuenta reservas del mismo servicio en la misma semana.
     */
    long countReservationsForServiceInWeek(
            UUID userId,
            UUID serviceId,
            LocalDate startOfWeek,
            LocalDate endOfWeek
    );

    /**
     * Lista reservas de un usuario.
     */
    List<Reservation> findByUserId(UUID userId);

    /**
     * RN-020: Busca reservas PENDING que expiraron (>24 horas).
     */
    List<Reservation> findExpiredPendingReservations();
}

