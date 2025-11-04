package com.rodrigo.curso.springboot.app.system_reservation_back.usecase.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de respuesta para Usuario.
 *
 * ¿Por qué @Builder de Lombok?
 * Para crear objetos de forma fluida:
 *
 * UserResponse response = UserResponse.builder()
 *     .id(user.getId())
 *     .email(user.getEmail().getValue())
 *     .name(user.getName())
 *     .build();
 *
 * Es más legible que:
 * UserResponse response = new UserResponse(id, email, name, ...);
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private UUID id;
    private String email;
    private String name;
    private String phone;
    private String role;        // "CLIENTE", "PROVEEDOR", "ADMIN"
    private String status;      // "ACTIVE", "INACTIVE", etc

    /**
     * Fecha de creación formateada.
     *
     * @JsonFormat especifica cómo se serializa a JSON.
     *
     * Ejemplo JSON:
     * {
     *   "createdAt": "2025-11-04 10:30:00"
     * }
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}

