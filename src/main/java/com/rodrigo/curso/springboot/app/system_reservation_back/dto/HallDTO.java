package com.rodrigo.curso.springboot.app.system_reservation_back.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class HallDTO {
    private Long id;

    @NotBlank(message = "El nombre de la sala es obligatorio")
    private String name;

    private String description;

    @NotNull(message = "La capacidad es obligatoria")
    @Min(value = 1, message = "La capacidad debe ser mayor a 0")
    private Integer capacity;

    @NotNull(message = "El precio por hora es obligatorio")
    @Min(value = 1, message = "El precio debe ser mayor a 0")
    private Double pricePerHour;

    private String location;
}
