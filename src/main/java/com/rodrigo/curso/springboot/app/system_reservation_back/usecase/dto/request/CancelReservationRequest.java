package com.rodrigo.curso.springboot.app.system_reservation_back.usecase.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para cancelar una reserva.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CancelReservationRequest {

    /**
     * Razón de cancelación.
     *
     * Es obligatoria para auditoría y análisis.
     */
    @NotBlank(message = "Cancellation reason is required")
    @Size(min = 10, max = 500, message = "Reason must be between 10 and 500 characters")
    private String reason;
}
