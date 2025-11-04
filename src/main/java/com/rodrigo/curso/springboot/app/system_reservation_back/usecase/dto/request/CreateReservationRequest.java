package com.rodrigo.curso.springboot.app.system_reservation_back.usecase.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/**
 * DTO para crear una reserva.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateReservationRequest {

    /**
     * ID del servicio a reservar.
     */
    @NotNull(message = "Service ID is required")
    private UUID serviceId;

    /**
     * Fecha de la reserva.
     *
     * @Future: Debe ser una fecha futura
     * @JsonFormat: Especifica el formato JSON esperado
     *
     * Ejemplo JSON:
     * {
     *   "date": "2025-11-05"
     * }
     */
    @NotNull(message = "Date is required")
    @Future(message = "Date must be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    /**
     * Hora de inicio.
     *
     * Ejemplo JSON:
     * {
     *   "startTime": "14:00"
     * }
     */
    @NotNull(message = "Start time is required")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    /**
     * Notas adicionales (opcional).
     */
    @Size(max = 500, message = "Notes must be less than 500 characters")
    private String notes;
}
