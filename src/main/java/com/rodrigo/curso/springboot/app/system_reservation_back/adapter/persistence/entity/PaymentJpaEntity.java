package com.rodrigo.curso.springboot.app.system_reservation_back.adapter.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * ENTIDAD JPA para persistir Payment en BD.
 *
 * Mapea con la tabla: payments
 */
@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentJpaEntity {

    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(name = "reservation_id", nullable = false, columnDefinition = "UUID")
    private UUID reservationId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;  // USD, EUR, etc

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;  // CREDIT_CARD, DEBIT_CARD, PAYPAL

    @Column(nullable = false)
    private String status;  // PENDING, COMPLETED, FAILED, REFUNDED

    @Column(name = "transaction_id")
    private String transactionId;  // ID del proveedor (Stripe, PayPal)

    @Column(name = "error_message", length = 500)
    private String errorMessage;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
