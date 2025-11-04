package com.rodrigo.curso.springboot.app.system_reservation_back.usecase.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de respuesta para Pago.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {

    private UUID id;
    private UUID reservationId;
    private BigDecimal amount;
    private String currency;
    private String status;           // PENDING, COMPLETED, FAILED, REFUNDED
    private String transactionId;    // ID del proveedor
    private String errorMessage;     // Si falló

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime processedAt;
}

