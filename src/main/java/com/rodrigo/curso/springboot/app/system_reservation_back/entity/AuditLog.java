package com.rodrigo.curso.springboot.app.system_reservation_back.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * ENTIDAD DE DOMINIO: AUDITLOG
 *
 * Registra TODAS las acciones en el sistema.
 *
 * Responsabilidades:
 * 1. Registrar QUÉ cambió (entity, campo)
 * 2. Registrar QUIÉN lo cambió (usuario)
 * 3. Registrar CUÁNDO se cambió (timestamp)
 * 4. Registrar CÓMO se cambió (valores antes/después)
 *
 * RN-031: Registro de todo cambio
 * RN-032: Datos de auditoría
 * RN-033: Inmutabilidad de auditoría
 * RN-034: Filtro de acceso
 *
 * IMPORTANTE: Esta entidad es de SOLO LECTURA una vez creada.
 * NUNCA se actualiza o se elimina un AuditLog.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class AuditLog {

    private UUID id;                              // ID único
    private UUID userId;                          // FK: Quién hizo la acción
    private String entityType;                    // Tipo de entidad (USER, RESERVATION, etc)
    private UUID entityId;                        // ID de la entidad modificada
    private String action;                        // CREATE, UPDATE, DELETE, STATE_CHANGE
    private String oldValues;                     // JSON con valores anteriores
    private String newValues;                     // JSON con valores nuevos
    private String ipAddress;                     // De dónde vino
    private String userAgent;                     // Navegador/cliente
    private LocalDateTime timestamp;              // Cuándo pasó

    /**
     * ENUM: Acciones posibles
     */
    public enum AuditAction {
        CREATE,         // Se creó una entidad
        UPDATE,         // Se modificó una entidad
        DELETE,         // Se eliminó una entidad
        STATE_CHANGE,   // Cambio de estado (PENDING → CONFIRMED)
        LOGIN,          // Usuario hizo login
        LOGOUT          // Usuario hizo logout
    }

    /**
     * ENUM: Tipos de entidades auditadas
     */
    public enum EntityType {
        USER,
        SERVICE,
        RESERVATION,
        PAYMENT,
        REVIEW,
        AUDITLOG
    }

    /**
     * CONSTRUCTOR PRIVADO
     *
     * Los AuditLog se crean pero NUNCA se modifican.
     */
    private AuditLog(
            UUID id,
            UUID userId,
            String entityType,
            UUID entityId,
            String action,
            String oldValues,
            String newValues,
            String ipAddress,
            String userAgent,
            LocalDateTime timestamp
    ) {
        this.id = id;
        this.userId = userId;
        this.entityType = entityType;
        this.entityId = entityId;
        this.action = action;
        this.oldValues = oldValues;
        this.newValues = newValues;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.timestamp = timestamp;
    }

    /**
     * FACTORY METHOD: Crear nuevo registro de auditoría
     *
     * RN-031: Registro automático de cambios
     *
     * @param userId Quién hizo el cambio
     * @param entityType Tipo de entidad
     * @param entityId ID de la entidad
     * @param action Qué acción se hizo
     * @param oldValues JSON con valores anteriores
     * @param newValues JSON con valores nuevos
     * @param ipAddress IP del usuario
     * @param userAgent Cliente/navegador
     * @return Nuevo AuditLog creado
     *
     * Ejemplo:
     * AuditLog log = AuditLog.create(
     *     userId,
     *     "RESERVATION",
     *     reservationId,
     *     "STATE_CHANGE",
     *     "{\"status\":\"PENDING\"}",
     *     "{\"status\":\"CONFIRMED\"}",
     *     "192.168.1.1",
     *     "Mozilla/5.0..."
     * );
     */
    public static AuditLog create(
            UUID userId,
            String entityType,
            UUID entityId,
            String action,
            String oldValues,
            String newValues,
            String ipAddress,
            String userAgent
    ) {
        return new AuditLog(
                UUID.randomUUID(),                      // ID único
                userId,
                entityType,
                entityId,
                action,
                oldValues,
                newValues,
                ipAddress,
                userAgent,
                LocalDateTime.now()                     // Timestamp actual
        );
    }

    /**
     * QUERY: ¿Esta es una eliminación?
     */
    public boolean isDelete() {
        return "DELETE".equals(action);
    }

    /**
     * QUERY: ¿Este es un cambio de estado?
     */
    public boolean isStateChange() {
        return "STATE_CHANGE".equals(action);
    }
}

