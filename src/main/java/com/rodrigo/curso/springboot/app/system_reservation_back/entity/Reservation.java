package com.rodrigo.curso.springboot.app.system_reservation_back.entity;

import com.rodrigo.curso.springboot.app.system_reservation_back.entity.exception.ReservationException;
import com.rodrigo.curso.springboot.app.system_reservation_back.entity.valuobject.DateRange;
import com.rodrigo.curso.springboot.app.system_reservation_back.entity.valuobject.ReservationStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * ENTIDAD DE DOMINIO: RESERVATION
 *
 * Representa una reserva realizada por un usuario sobre un servicio.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class Reservation {

    private UUID id;
    private UUID userId;
    private UUID serviceId;
    private DateRange dateRange;
    private ReservationStatus status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * CONSTRUCTOR PRIVADO
     */
    private Reservation(
            UUID id,
            UUID userId,
            UUID serviceId,
            DateRange dateRange,
            ReservationStatus status,
            String notes,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.userId = userId;
        this.serviceId = serviceId;
        this.dateRange = dateRange;
        this.status = status;
        this.notes = notes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * FACTORY METHOD: Crear nueva reserva
     */
    public static Reservation create(
            UUID userId,
            UUID serviceId,
            DateRange dateRange,
            String notes
    ) {
        if (userId == null || serviceId == null || dateRange == null) {
            throw new ReservationException("User, Service and DateRange must not be null");
        }

        LocalDateTime now = LocalDateTime.now();

        return new Reservation(
                UUID.randomUUID(),
                userId,
                serviceId,
                dateRange,
                ReservationStatus.PENDING,
                notes,
                now,
                now
        );
    }

    /**
     * FACTORY METHOD: Reconstruir desde la base de datos
     */
    public static Reservation reconstructFromDatabase(
            UUID id,
            UUID userId,
            UUID serviceId,
            DateRange dateRange,
            ReservationStatus status,
            String notes,
            LocalDateTime createdAt
    ) {
        LocalDateTime updatedAt = LocalDateTime.now();
        return new Reservation(
                id,
                userId,
                serviceId,
                dateRange,
                status,
                notes,
                createdAt,
                updatedAt
        );
    }

    /**
     * OPERACIÓN: Confirmar reserva
     */
    public void confirm() {
        if (status != ReservationStatus.PENDING) {
            throw new ReservationException("Only pending reservations can be confirmed");
        }
        this.status = ReservationStatus.CONFIRMED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * OPERACIÓN: Cancelar reserva
     */
    public void cancel() {
        if (status == ReservationStatus.CANCELLED) {
            throw new ReservationException("Reservation already cancelled");
        }
        this.status = ReservationStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * COMPORTAMIENTO: Verificar si esta reserva se solapa con otra
     *
     * @param other otra reserva a comparar
     * @return true si los rangos de tiempo se superponen
     */
    public boolean overlaps(Reservation other) {
        if (other == null || this.dateRange == null || other.dateRange == null) {
            return false;
        }

        // Solo comparar si son del mismo día
        if (!this.dateRange.getDate().equals(other.dateRange.getDate())) {
            return false;
        }

        // Comparar los rangos de tiempo
        return this.dateRange.overlaps(other.dateRange);
    }

    // =============================================================
    // 🔹 MÉTODOS DE ESTADO — usados por casos de uso (como ProcessPaymentUseCase)
    // =============================================================

    public boolean isPending() {
        return this.status == ReservationStatus.PENDING;
    }

    public boolean isConfirmed() {
        return this.status == ReservationStatus.CONFIRMED;
    }

    public boolean isCancelled() {
        return this.status == ReservationStatus.CANCELLED;
    }
}
