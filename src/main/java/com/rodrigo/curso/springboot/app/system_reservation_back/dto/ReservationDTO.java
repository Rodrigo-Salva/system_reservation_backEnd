package com.rodrigo.curso.springboot.app.system_reservation_back.dto;

import com.rodrigo.curso.springboot.app.system_reservation_back.entity.ReserveState;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservationDTO {
    private Long id;

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long userId;

    @NotNull(message = "El ID de la sala es obligatorio")
    private Long hallId;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @Future(message = "La fecha de inicio debe ser en el futuro")
    private LocalDateTime startDate;

    @NotNull(message = "La fecha de fin es obligatoria")
    @Future(message = "La fecha de fin debe ser en el futuro")
    private LocalDateTime endDate;

    private ReserveState state = ReserveState.CONFIRMADA;

    private String grades;
}