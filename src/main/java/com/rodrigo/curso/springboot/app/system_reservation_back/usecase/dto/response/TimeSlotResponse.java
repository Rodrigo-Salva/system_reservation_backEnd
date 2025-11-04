package com.rodrigo.curso.springboot.app.system_reservation_back.usecase.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/**
 * DTO para un slot de tiempo individual.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeSlotResponse {

    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    private boolean available;  // true si está disponible
}

