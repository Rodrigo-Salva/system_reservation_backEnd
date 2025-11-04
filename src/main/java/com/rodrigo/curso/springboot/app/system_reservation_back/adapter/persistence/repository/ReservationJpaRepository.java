package com.rodrigo.curso.springboot.app.system_reservation_back.adapter.persistence.repository;

import com.rodrigo.curso.springboot.app.system_reservation_back.adapter.persistence.entity.ReservationJpaEntity;
import com.rodrigo.curso.springboot.app.system_reservation_back.entity.valuobject.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/**
 * SPRING DATA REPOSITORY para Reservation.
 */
@Repository
public interface ReservationJpaRepository extends JpaRepository<ReservationJpaEntity, UUID> {

    /**
     * RN-015: Buscar reservas que podrían solapar.
     *
     * Query personalizado con @Query (SQL nativo o JPQL).
     *
     * @param serviceId Servicio
     * @param date Fecha
     * @param startTime Hora inicio
     * @param endTime Hora fin
     * @return Reservas del mismo servicio que se solapan
     */
    @Query("""
        SELECT r FROM ReservationJpaEntity r
        WHERE r.serviceId = :serviceId
        AND r.date = :date
        AND r.status IN ('CONFIRMED', 'COMPLETED')
        AND r.startTime < :endTime
        AND r.endTime > :startTime
    """)
    List<ReservationJpaEntity> findOverlappingReservations(
            @Param("serviceId") UUID serviceId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );

    /**
     * RN-021: Contar reservas CONFIRMED activas del usuario.
     */
    @Query("""
        SELECT COUNT(r) FROM ReservationJpaEntity r
        WHERE r.userId = :userId
        AND r.status = 'CONFIRMED'
        AND r.date >= CAST(CURRENT_DATE AS LocalDate)
    """)
    long countActiveReservationsByUser(@Param("userId") UUID userId);

    /**
     * RN-022: Contar reservas por servicio en la semana.
     */
    @Query("""
        SELECT COUNT(r) FROM ReservationJpaEntity r
        WHERE r.userId = :userId
        AND r.serviceId = :serviceId
        AND r.date BETWEEN :startOfWeek AND :endOfWeek
        AND r.status IN ('PENDING', 'CONFIRMED')
    """)
    long countReservationsForServiceInWeek(
            @Param("userId") UUID userId,
            @Param("serviceId") UUID serviceId,
            @Param("startOfWeek") LocalDate startOfWeek,
            @Param("endOfWeek") LocalDate endOfWeek
    );

    /**
     * Buscar reservas de un usuario.
     */
    List<ReservationJpaEntity> findByUserId(UUID userId);

    /**
     * RN-020: Buscar reservas PENDING que expiraron (>24 horas).
     */
    @Query("""
        SELECT r FROM ReservationJpaEntity r
        WHERE r.status = 'PENDING'
        AND r.createdAt < :expirationTime
    """)
    List<ReservationJpaEntity> findExpiredPendingReservations(
            @Param("expirationTime") LocalDateTime expirationTime
    );
}

