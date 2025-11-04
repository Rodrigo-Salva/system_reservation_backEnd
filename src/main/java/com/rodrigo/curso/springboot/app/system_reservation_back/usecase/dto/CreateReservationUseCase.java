package com.rodrigo.curso.springboot.app.system_reservation_back.usecase.dto;

import com.rodrigo.curso.springboot.app.system_reservation_back.entity.Reservation;
import com.rodrigo.curso.springboot.app.system_reservation_back.entity.Service;
import com.rodrigo.curso.springboot.app.system_reservation_back.entity.User;
import com.rodrigo.curso.springboot.app.system_reservation_back.entity.exception.ReservationException;
import com.rodrigo.curso.springboot.app.system_reservation_back.entity.exception.UserException;
import com.rodrigo.curso.springboot.app.system_reservation_back.entity.valuobject.DateRange;
import com.rodrigo.curso.springboot.app.system_reservation_back.usecase.dto.request.CreateReservationRequest;
import com.rodrigo.curso.springboot.app.system_reservation_back.usecase.dto.response.ReservationResponse;
import com.rodrigo.curso.springboot.app.system_reservation_back.usecase.gateway.ReservationGateway;
import com.rodrigo.curso.springboot.app.system_reservation_back.usecase.gateway.*;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/**
 * USE CASE: Crear una nueva reserva.
 *
 * RESPONSABILIDADES:
 * 1. Validar que usuario existe y está activo
 * 2. Validar que servicio existe y está activo
 * 3. RN-015: Validar que no haya solapamiento
 * 4. RN-021: Validar límite de reservas activas
 * 5. RN-022: Validar límite por semana
 * 6. Crear entidad Reservation (valida RN-016, RN-017)
 * 7. Persistir
 * 8. Retornar DTO
 *
 * Este es el Use Case MÁS COMPLEJO porque tiene
 * muchas validaciones de reglas de negocio.
 */
@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class CreateReservationUseCase {

    // DEPENDENCIAS
    private final ReservationGateway reservationGateway;
    private final ServiceGateway serviceGateway;
    private final UserGateway userGateway;

    /**
     * EJECUTA el caso de uso.
     *
     * @param userId ID del usuario que hace la reserva
     * @param request DTO con datos de la reserva
     * @return DTO con la reserva creada
     * @throws ReservationException si alguna validación falla
     */
    @Transactional
    public ReservationResponse execute(UUID userId, CreateReservationRequest request) {

        // PASO 1: Validar que el usuario existe y está activo
        User user = (User) userGateway.findById(userId)
                .orElseThrow(() -> UserException.notFound(userId.toString()));

        if (!user.isActive()) {
            throw new ReservationException("User is not active");
        }

        // PASO 2: Validar que el servicio existe y está activo
        Service service = (Service) serviceGateway.findById(request.getServiceId())
                .orElseThrow(() -> new ReservationException(
                        "Service not found: " + request.getServiceId()
                ));

        if (!service.isAvailable()) {
            throw new ReservationException("Service is not available");
        }

        // PASO 3: Calcular end time (start + duration del servicio)
        LocalTime endTime = request.getStartTime()
                .plusMinutes(service.getDurationMinutes());

        // PASO 4: Crear DateRange (valida RN-016: anticipación mínima)
        DateRange dateRange = new DateRange(
                request.getDate(),
                request.getStartTime(),
                endTime
        );

        // PASO 5: RN-015 - VALIDAR QUE NO HAYA SOLAPAMIENTO
        validateNoOverlap(request.getServiceId(), dateRange);

        // PASO 6: RN-021 - Validar límite de reservas activas (máx 5)
        validateActiveReservationsLimit(userId);

        // PASO 7: RN-022 - Validar límite por semana (máx 3 del mismo servicio)
        validateWeeklyLimit(userId, request.getServiceId(), request.getDate());

        // PASO 8: Crear entidad Reservation
        // El factory method valida:
        // - RN-016: Anticipación mínima (24 horas)
        // - RN-017: No en el pasado
        Reservation reservation = Reservation.create(
                userId,
                request.getServiceId(),
                dateRange,
                request.getNotes()
        );

        // PASO 9: Persistir en BD
        Reservation savedReservation = reservationGateway.save(reservation);

        // PASO 10: Convertir a DTO y retornar
        return mapToResponse(savedReservation, user, service);
    }

    /**
     * RN-015: VALIDA QUE NO HAYA SOLAPAMIENTO
     *
     * Busca reservas CONFIRMED o COMPLETED del mismo servicio
     * que se solapen con el horario solicitado.
     *
     * @throws ReservationException si hay solapamiento
     */
    private void validateNoOverlap(UUID serviceId, DateRange dateRange) {
        // Buscar reservas que podrían solapar
        List<Reservation> overlapping = reservationGateway
                .findOverlappingReservations(
                        serviceId,
                        dateRange.getDate(),
                        dateRange.getStartTime(),
                        dateRange.getEndTime()
                );

        // Validar si alguna solapa
        for (Reservation existing : overlapping) {
            if (existing.overlaps(
                    Reservation.create(
                            UUID.randomUUID(),
                            serviceId,
                            dateRange,
                            ""
                    )
            )) {
                throw ReservationException.timeOverlap();
            }
        }
    }

    /**
     * RN-021: VALIDA LÍMITE DE RESERVAS ACTIVAS
     *
     * Un usuario no puede tener más de 5 reservas CONFIRMED activas.
     *
     * @throws ReservationException si ya tiene 5 o más
     */
    private void validateActiveReservationsLimit(UUID userId) {
        long activeCount = reservationGateway.countActiveReservationsByUser(userId);

        if (activeCount >= 5) {
            throw new ReservationException(
                    "Maximum active reservations reached (5). " +
                            "Complete or cancel one to create a new reservation."
            );
        }
    }

    /**
     * RN-022: VALIDA LÍMITE SEMANAL
     *
     * Un usuario no puede reservar el mismo servicio
     * más de 3 veces en la misma semana.
     *
     * @throws ReservationException si ya tiene 3 o más
     */
    private void validateWeeklyLimit(UUID userId, UUID serviceId, LocalDate date) {
        // Calcular inicio y fin de semana
        LocalDate startOfWeek = date.minusDays(date.getDayOfWeek().getValue() - 1);
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        // Contar reservas de este servicio en esta semana
        long weeklyCount = reservationGateway.countReservationsForServiceInWeek(
                userId,
                serviceId,
                startOfWeek,
                endOfWeek
        );

        if (weeklyCount >= 3) {
            throw new ReservationException(
                    "Maximum weekly reservations reached for this service (3). " +
                            "Wait until next week to book again."
            );
        }
    }

    /**
     * MAPPER: Convierte Entity a DTO
     */
    private ReservationResponse mapToResponse(
            Reservation reservation,
            User user,
            Service service
    ) {
        return ReservationResponse.builder()
                .id(reservation.getId())
                .userId(reservation.getUserId())
                .serviceId(reservation.getServiceId())
                .date(reservation.getDateRange().getDate())
                .startTime(reservation.getDateRange().getStartTime())
                .endTime(reservation.getDateRange().getEndTime())
                .status(reservation.getStatus().name())
                .notes(reservation.getNotes())
                .serviceName(service.getName())      // Info adicional
                .userName(user.getName())            // Info adicional
                .createdAt(reservation.getCreatedAt())
                .build();
    }
}

