package com.rodrigo.curso.springboot.app.system_reservation_back.usecase.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * DTO de respuesta para Disponibilidad.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvailabilityResponse {

    private UUID serviceId;
    private String serviceName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    private Long totalSlots;        // Total de slots en el día
    private Long availableSlots;    // Cuántos están disponibles
    private List<TimeSlotResponse> timeSlots;  // Detalle de cada slot
}

