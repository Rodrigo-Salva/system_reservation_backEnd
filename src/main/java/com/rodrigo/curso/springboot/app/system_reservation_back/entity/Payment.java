package com.rodrigo.curso.springboot.app.system_reservation_back.entity;

import com.rodrigo.curso.springboot.app.system_reservation_back.entity.exception.PaymentException;
import com.rodrigo.curso.springboot.app.system_reservation_back.entity.valuobject.Money;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * ENTIDAD DE DOMINIO: PAYMENT
 *
 * Representa un PAGO en el sistema.
 *
 * Responsabilidades:
 * 1. Mantener info del pago
 * 2. Validar transiciones de estado
 * 3. Registrar auditoría del pago
 *
 * RN-026: Pago obligatorio para confirmar reserva
 * RN-027: Monto válido
 * RN-028: Pago exitoso
 * RN-029: Transacción atómica
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class Payment {

    private UUID id;                              // ID único
    private UUID reservationId;                   // FK: Reserva asociada
    private Money amount;                         // Cantidad (Value Object)
    private String paymentMethod;                 // Método (CREDIT_CARD, DEBIT_CARD, PAYPAL)
    private PaymentStatus status;                 // Estado del pago
    private String transactionId;                 // ID del proveedor de pago
    private String errorMessage;                  // Mensaje de error (si falla)
    private LocalDateTime processedAt;            // Cuándo se procesó
    private LocalDateTime createdAt;              // Cuándo se creó

    /**
     * ENUM: Estados posibles de un pago
     */
    public enum PaymentStatus {
        PENDING,
        COMPLETED,
        FAILED,
        REFUNDED
    }

    /**
     * ENUM: Métodos de pago
     */
    public enum PaymentMethod {
        CREDIT_CARD,
        DEBIT_CARD,
        PAYPAL,
        STRIPE
    }

    /**
     * CONSTRUCTOR PRIVADO
     */
    private Payment(
            UUID id,
            UUID reservationId,
            Money amount,
            String paymentMethod,
            PaymentStatus status,
            String transactionId,
            String errorMessage,
            LocalDateTime processedAt,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.reservationId = reservationId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.transactionId = transactionId;
        this.errorMessage = errorMessage;
        this.processedAt = processedAt;
        this.createdAt = createdAt;
    }

    /**
     * FACTORY METHOD: Crear nuevo pago
     */
    public static Payment create(
            UUID reservationId,
            Money amount,
            String paymentMethod
    ) {
        return new Payment(
                UUID.randomUUID(),
                reservationId,
                amount,
                paymentMethod,
                PaymentStatus.PENDING,
                null,
                null,
                null,
                LocalDateTime.now()
        );
    }

    /**
     * FACTORY METHOD: Reconstruir desde la base de datos
     *
     * Se usa cuando cargamos un Payment existente desde JPA.
     */
    public static Payment reconstructFromDatabase(
            UUID id,
            UUID reservationId,
            Money amount,
            String paymentMethod,
            PaymentStatus status,
            String transactionId,
            String errorMessage,
            LocalDateTime processedAt,
            LocalDateTime createdAt
    ) {
        return new Payment(
                id,
                reservationId,
                amount,
                paymentMethod,
                status,
                transactionId,
                errorMessage,
                processedAt,
                createdAt
        );
    }

    /**
     * OPERACIÓN: Marcar pago como completado
     */
    public void markAsCompleted(String transactionId) {
        if (this.status != PaymentStatus.PENDING) {
            throw PaymentException.processingFailed(
                    "Cannot complete a payment that is not PENDING"
            );
        }

        this.status = PaymentStatus.COMPLETED;
        this.transactionId = transactionId;
        this.processedAt = LocalDateTime.now();
        this.errorMessage = null;
    }

    /**
     * OPERACIÓN: Marcar pago como fallido
     */
    public void markAsFailed(String errorMessage) {
        if (this.status != PaymentStatus.PENDING) {
            throw PaymentException.processingFailed(
                    "Cannot fail a payment that is not PENDING"
            );
        }

        this.status = PaymentStatus.FAILED;
        this.errorMessage = errorMessage;
        this.processedAt = LocalDateTime.now();
    }

    /**
     * OPERACIÓN: Reembolsar pago
     */
    public void refund() {
        if (this.status != PaymentStatus.COMPLETED) {
            throw PaymentException.processingFailed(
                    "Cannot refund a payment that is not COMPLETED"
            );
        }

        this.status = PaymentStatus.REFUNDED;
        this.processedAt = LocalDateTime.now();
    }

    /**
     * QUERIES
     */
    public boolean isCompleted() {
        return this.status == PaymentStatus.COMPLETED;
    }

    public boolean isFailed() {
        return this.status == PaymentStatus.FAILED;
    }

    public boolean isPending() {
        return this.status == PaymentStatus.PENDING;
    }

    public boolean isRefunded() {
        return this.status == PaymentStatus.REFUNDED;
    }
}