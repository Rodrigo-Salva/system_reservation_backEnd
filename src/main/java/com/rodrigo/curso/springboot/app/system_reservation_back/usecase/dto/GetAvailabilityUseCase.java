package com.rodrigo.curso.springboot.app.system_reservation_back.usecase.dto;

import com.rodrigo.curso.springboot.app.system_reservation_back.entity.Reservation;
import com.rodrigo.curso.springboot.app.system_reservation_back.entity.Service;
import com.rodrigo.curso.springboot.app.system_reservation_back.entity.valuobject.DateRange;
import com.rodrigo.curso.springboot.app.system_reservation_back.usecase.dto.response.AvailabilityResponse;
import com.rodrigo.curso.springboot.app.system_reservation_back.usecase.dto.response.TimeSlotResponse;
import com.rodrigo.curso.springboot.app.system_reservation_back.usecase.gateway.ReservationGateway;
import com.rodrigo.curso.springboot.app.system_reservation_back.usecase.gateway.ServiceGateway;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * USE CASE: Obtener horarios disponibles de un servicio.
 */
@org.springframework.stereotype.Service  // ← Usar fully qualified name
@RequiredArgsConstructor
public class GetAvailabilityUseCase {

    private final ServiceGateway serviceGateway;
    private final ReservationGateway reservationGateway;

    public AvailabilityResponse execute(UUID serviceId, LocalDate date) {

        // PASO 1: Validar que el servicio existe
        Service service = (Service) serviceGateway.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service not found"));

        // PASO 2: Verificar que el servicio está activo
        if (!service.isAvailable()) {
            throw new RuntimeException("Service is not available");
        }

        // PASO 3: RN-012-014 - Obtener horarios de operación
        LocalTime openingTime = LocalTime.of(9, 0);
        LocalTime closingTime = LocalTime.of(18, 0);

        // PASO 4: RN-015 - Buscar reservas ya hechas
        List<Reservation> existingReservations =
                reservationGateway.findOverlappingReservations(
                        serviceId,
                        date,
                        openingTime,
                        closingTime
                );

        // PASO 5: Generar slots
        List<TimeSlotResponse> timeSlots = generateTimeSlots(
                service,
                date,
                openingTime,
                closingTime,
                existingReservations
        );

        // PASO 6: Retornar DTO
        return AvailabilityResponse.builder()
                .serviceId(serviceId)
                .serviceName(service.getName())
                .date(date)
                .totalSlots((long) timeSlots.size())
                .availableSlots(
                        timeSlots.stream()
                                .filter(TimeSlotResponse::isAvailable)
                                .count()
                )
                .timeSlots(timeSlots)
                .build();
    }

    private List<TimeSlotResponse> generateTimeSlots(
            Service service,
            LocalDate date,
            LocalTime openingTime,
            LocalTime closingTime,
            List<Reservation> existingReservations
    ) {
        List<TimeSlotResponse> slots = new ArrayList<>();
        LocalTime currentTime = openingTime;
        int durationMinutes = service.getDurationMinutes();

        while (currentTime.plusMinutes(durationMinutes).isBefore(closingTime) ||
                currentTime.plusMinutes(durationMinutes).equals(closingTime)) {

            LocalTime endTime = currentTime.plusMinutes(durationMinutes);
            DateRange slotRange = new DateRange(date, currentTime, endTime);

            boolean isAvailable = true;
            for (Reservation existing : existingReservations) {
                if (existing.overlaps(
                        Reservation.create(
                                UUID.randomUUID(),
                                service.getId(),
                                slotRange,
                                ""
                        )
                )) {
                    isAvailable = false;
                    break;
                }
            }

            if (isAvailable) {
                long count = existingReservations.stream()
                        .filter(res -> !res.overlaps(
                                Reservation.create(
                                        UUID.randomUUID(),
                                        service.getId(),
                                        slotRange,
                                        ""
                                )
                        ))
                        .count();

                if (count >= service.getMaxConcurrentReservations()) {
                    isAvailable = false;
                }
            }

            slots.add(TimeSlotResponse.builder()
                    .startTime(currentTime)
                    .endTime(endTime)
                    .available(isAvailable)
                    .build()
            );

            currentTime = endTime;
        }

        return slots;
    }
}