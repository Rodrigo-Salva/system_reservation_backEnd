package com.rodrigo.curso.springboot.app.system_reservation_back.adapter.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * ENTIDAD JPA para persistir AuditLog en BD.
 *
 * De SOLO LECTURA una vez creada.
 */
@Entity
@Table(name = "audit_logs", indexes = {
        @Index(name = "idx_entity_id", columnList = "entity_id"),
        @Index(name = "idx_entity_type", columnList = "entity_type"),
        @Index(name = "idx_timestamp", columnList = "timestamp")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogJpaEntity {

    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(name = "user_id", columnDefinition = "UUID")
    private UUID userId;

    @Column(name = "entity_type", nullable = false)
    private String entityType;  // USER, RESERVATION, PAYMENT, etc

    @Column(name = "entity_id", nullable = false, columnDefinition = "UUID")
    private UUID entityId;

    @Column(nullable = false)
    private String action;  // CREATE, UPDATE, DELETE, STATE_CHANGE

    @Column(name = "old_values", columnDefinition = "TEXT")
    private String oldValues;  // JSON

    @Column(name = "new_values", columnDefinition = "TEXT")
    private String newValues;  // JSON

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(nullable = false)
    private LocalDateTime timestamp;
}
