package com.rodrigo.curso.springboot.app.system_reservation_back.adapter.persistence.entity;

import com.rodrigo.curso.springboot.app.system_reservation_back.common.mapper.ReservationMapper;
import com.rodrigo.curso.springboot.app.system_reservation_back.entity.Reservation;
import com.rodrigo.curso.springboot.app.system_reservation_back.entity.valuobject.ReservationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

/**
 * ENTIDAD JPA para persistir Reserva en BD.
 *
 * Mapea con la tabla: reservations
 */
@Entity
@Table(name = "reservations", indexes = {
        @Index(name = "idx_reservation_service_date", columnList = "service_id, date, start_time, end_time"),
        @Index(name = "idx_reservation_user", columnList = "user_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationJpaEntity {

    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(name = "user_id", nullable = false, columnDefinition = "UUID")
    private UUID userId;

    @Column(name = "service_id", nullable = false, columnDefinition = "UUID")
    private UUID serviceId;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    @Column(length = 500)
    private String notes;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * CONVERTIR: JpaEntity → Entity de Dominio
     */
    public Reservation toDomain() {
        return new ReservationMapper().toDomain(this);
    }

    /**
     * CONVERTIR: Entity de Dominio → JpaEntity
     */
    public static ReservationJpaEntity fromDomain(Reservation domain) {
        return new ReservationMapper().toEntity(domain);
    }
}
