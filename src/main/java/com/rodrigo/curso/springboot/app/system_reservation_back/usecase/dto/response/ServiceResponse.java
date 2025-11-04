package com.rodrigo.curso.springboot.app.system_reservation_back.usecase.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO de respuesta para Servicio.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceResponse {

    private UUID id;
    private String name;
    private String description;
    private Integer durationMinutes;
    private BigDecimal price;
    private String currency;
    private Integer maxConcurrentReservations;
    private Boolean isActive;

    // Información adicional
    private String providerName;  // Nombre del proveedor
}

