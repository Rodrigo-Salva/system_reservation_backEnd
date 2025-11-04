package com.rodrigo.curso.springboot.app.system_reservation_back.entity;

import com.rodrigo.curso.springboot.app.system_reservation_back.entity.exception.ReservationException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * ENTIDAD DE DOMINIO: REVIEW
 *
 * Representa una RESEÑA/CALIFICACIÓN de un servicio.
 *
 * RN-035: Crear review
 * RN-036: Validación de review
 * RN-037: Cálculo de promedio
 * RN-038: Visibilidad de reviews
 * RN-039: Eliminación de review
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class Review {

    private UUID id;                              // ID único
    private UUID reservationId;                   // FK: Reserva asociada
    private UUID serviceId;                       // FK: Servicio siendo reseñado
    private UUID userId;                          // FK: Quién hizo la reseña
    private Integer rating;                       // Calificación 1-5 estrellas
    private String title;                         // Título de la reseña
    private String comment;                       // Comentario detallado
    private ReviewStatus status;                  // Estado (PENDING, PUBLISHED, DELETED)
    private LocalDateTime createdAt;              // Cuándo se creó
    private LocalDateTime updatedAt;              // Última actualización
    private LocalDateTime deletedAt;              // Cuándo se eliminó (soft delete)

    /**
     * ENUM: Estados de una reseña
     *
     * PENDING   → Creada, esperando aprobación del admin
     * PUBLISHED → Visible públicamente
     * DELETED   → Soft deleted (no visible)
     */
    public enum ReviewStatus {
        PENDING,
        PUBLISHED,
        DELETED
    }

    /**
     * CONSTRUCTOR PRIVADO
     */
    private Review(
            UUID id,
            UUID reservationId,
            UUID serviceId,
            UUID userId,
            Integer rating,
            String title,
            String comment,
            ReviewStatus status,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.reservationId = reservationId;
        this.serviceId = serviceId;
        this.userId = userId;
        this.rating = rating;
        this.title = title;
        this.comment = comment;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
        this.deletedAt = null;
    }

    /**
     * FACTORY METHOD: Crear nueva reseña
     *
     * RN-035: Solo después que reserva está COMPLETED
     * RN-036: Validar rating y comentarios
     *
     * @param reservationId ID de la reserva
     * @param serviceId ID del servicio
     * @param userId Quién hace la reseña
     * @param rating Calificación 1-5
     * @param title Título
     * @param comment Comentario
     * @return Nueva reseña en estado PENDING
     * @throws ReservationException si validación falla
     *
     * Ejemplo:
     * Review review = Review.create(
     *     reservationId,
     *     serviceId,
     *     userId,
     *     5,
     *     "Excelente servicio",
     *     "El médico fue muy atento y profesional"
     * );
     */
    public static Review create(
            UUID reservationId,
            UUID serviceId,
            UUID userId,
            Integer rating,
            String title,
            String comment
    ) {
        // RN-036: Validar rating 1-5
        if (rating == null || rating < 1 || rating > 5) {
            throw new ReservationException("Rating must be between 1 and 5");
        }

        // RN-036: Validar título
        if (title == null || title.isBlank() ||
                title.length() < 5 || title.length() > 100) {
            throw new ReservationException(
                    "Title must be between 5 and 100 characters"
            );
        }

        // RN-036: Validar comentario (opcional pero si se da, validar)
        if (comment != null && (comment.length() < 10 || comment.length() > 1000)) {
            throw new ReservationException(
                    "Comment must be between 10 and 1000 characters"
            );
        }

        return new Review(
                UUID.randomUUID(),                      // ID único
                reservationId,
                serviceId,
                userId,
                rating,
                title,
                comment,
                ReviewStatus.PENDING,                   // RN-038: Espera aprobación
                LocalDateTime.now()                     // Fecha creación
        );
    }

    /**
     * OPERACIÓN: Publicar reseña
     *
     * El ADMIN aprueba la reseña y la hace pública.
     *
     * PENDING → PUBLISHED
     */
    public void publish() {
        if (this.status != ReviewStatus.PENDING) {
            throw new ReservationException(
                    "Cannot publish a review that is not PENDING"
            );
        }
        this.status = ReviewStatus.PUBLISHED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * OPERACIÓN: Eliminar reseña (soft delete)
     *
     * RN-039: Eliminación de review
     *
     * No se borra de BD, solo se marca como DELETED.
     * Esto es para mantener auditoría.
     */
    public void delete() {
        this.status = ReviewStatus.DELETED;
        this.deletedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * QUERIES
     */
    public boolean isPublished() {
        return this.status == ReviewStatus.PUBLISHED;
    }

    public boolean isDeleted() {
        return this.status == ReviewStatus.DELETED;
    }

    public boolean isPending() {
        return this.status == ReviewStatus.PENDING;
    }
}

