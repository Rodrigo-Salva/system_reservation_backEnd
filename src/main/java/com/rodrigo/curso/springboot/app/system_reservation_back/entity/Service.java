package com.rodrigo.curso.springboot.app.system_reservation_back.entity;

import com.rodrigo.curso.springboot.app.system_reservation_back.entity.exception.ReservationException;
import com.rodrigo.curso.springboot.app.system_reservation_back.entity.valuobject.Money;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * ENTIDAD DE DOMINIO: SERVICE
 *
 * Representa un SERVICIO que puede ser reservado.
 *
 * Ejemplos:
 * - Consulta médica de 30 minutos
 * - Sesión de entrenamiento de 1 hora
 * - Renta de sala de conferencias de 2 horas
 *
 * Responsabilidades:
 * 1. Mantener datos del servicio
 * 2. Validar reglas de duración y capacidad
 * 3. Controlar si el servicio está activo
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class Service {

    private UUID id;                              // ID único
    private UUID providerId;                      // FK: Quién ofrece el servicio
    private String name;                          // Nombre (ej: "Consulta Médica")
    private String description;                   // Descripción
    private Integer durationMinutes;              // Duración en minutos
    private Money price;                          // Precio (Value Object)
    private Integer maxConcurrentReservations;    // Máximo simultáneas (RN-011)
    private Boolean isActive;                     // ¿Está disponible?
    private LocalDateTime createdAt;              // Fecha de creación

    /**
     * RN-009: DURACIONES VÁLIDAS
     */
    private static final Map<Integer, Integer> VALID_DURATIONS = new HashMap<>();
    static {
        VALID_DURATIONS.put(15, 15);
        VALID_DURATIONS.put(30, 30);
        VALID_DURATIONS.put(45, 45);
        VALID_DURATIONS.put(60, 60);
        VALID_DURATIONS.put(90, 90);
        VALID_DURATIONS.put(120, 120);
        VALID_DURATIONS.put(240, 240);
        VALID_DURATIONS.put(480, 480);
    }

    /**
     * CONSTRUCTOR PRIVADO
     */
    private Service(
            UUID id,
            UUID providerId,
            String name,
            String description,
            Integer durationMinutes,
            Money price,
            Integer maxConcurrentReservations,
            Boolean isActive,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.providerId = providerId;
        this.name = name;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.price = price;
        this.maxConcurrentReservations = maxConcurrentReservations;
        this.isActive = isActive != null && isActive;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    /**
     * FACTORY METHOD: Crear nuevo servicio
     *
     * RN-007: Creación de servicio (solo PROVEEDOR o ADMIN)
     * RN-009: Duración válida
     * RN-008: Precio válido
     * RN-011: Máximo de reservas simultáneas
     */
    public static Service create(
            UUID providerId,
            String name,
            String description,
            Integer durationMinutes,
            Money price,
            Integer maxConcurrentReservations
    ) {
        if (!VALID_DURATIONS.containsKey(durationMinutes)) {
            throw new ReservationException(
                    "Duration must be one of: " + VALID_DURATIONS.keySet() +
                            ". Got: " + durationMinutes
            );
        }

        if (maxConcurrentReservations < 1) {
            throw new ReservationException(
                    "Max concurrent reservations must be at least 1"
            );
        }

        return new Service(
                UUID.randomUUID(),
                providerId,
                name,
                description,
                durationMinutes,
                price,
                maxConcurrentReservations,
                true, // activo por defecto
                LocalDateTime.now()
        );
    }

    /**
     * FACTORY METHOD: Reconstruir desde base de datos
     *
     * Usado por los mappers (JPA → Dominio)
     */
    public static Service reconstructFromDatabase(
            UUID id,
            UUID providerId,
            String name,
            String description,
            Integer durationMinutes,
            Money price,
            Integer maxConcurrentReservations,
            Boolean isActive,
            LocalDateTime createdAt
    ) {
        return new Service(
                id,
                providerId,
                name,
                description,
                durationMinutes,
                price,
                maxConcurrentReservations,
                isActive,
                createdAt
        );
    }

    /**
     * OPERACIÓN: Desactivar servicio
     */
    public void deactivate() {
        this.isActive = false;
    }

    /**
     * OPERACIÓN: Activar servicio
     */
    public void activate() {
        this.isActive = true;
    }

    /**
     * OPERACIÓN: Actualizar precio
     */
    public void updatePrice(Money newPrice) {
        this.price = newPrice;
    }

    /**
     * QUERY: ¿El servicio está disponible?
     */
    public boolean isAvailable() {
        return Boolean.TRUE.equals(isActive);
    }

    /**
     * QUERY: Getter explícito compatible con método reference (Service::isActive)
     */
    public boolean isActive() {
        return Boolean.TRUE.equals(isActive);
    }
}
