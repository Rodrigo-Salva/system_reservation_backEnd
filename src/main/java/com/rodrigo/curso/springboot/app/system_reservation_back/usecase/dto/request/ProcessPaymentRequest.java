package com.rodrigo.curso.springboot.app.system_reservation_back.usecase.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO para procesar un pago.
 *
 * Contiene los datos necesarios para procesar un pago
 * con un proveedor (Stripe, PayPal, etc).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProcessPaymentRequest {

    /**
     * Monto a pagar.
     *
     * @NotNull: Obligatorio
     * @DecimalMin: Debe ser > 0
     *
     * Ejemplo JSON:
     * {
     *   "amount": "50.00"
     * }
     */
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    /**
     * Método de pago.
     *
     * Valores válidos:
     * - CREDIT_CARD
     * - DEBIT_CARD
     * - PAYPAL
     * - STRIPE
     *
     * @NotBlank: No puede ser vacío
     */
    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    /**
     * Token de pago del proveedor.
     *
     * Ejemplo con Stripe:
     * - pm_1234567890 (payment method ID)
     * - stripe_token_xyz (token de tarjeta)
     *
     * En producción, NUNCA recibes el número de tarjeta directamente.
     * El cliente envía un TOKEN que creó Stripe.
     */
    @NotBlank(message = "Payment token is required")
    private String paymentToken;

    /**
     * ID del cliente en el proveedor de pago (opcional).
     *
     * Ejemplo con Stripe:
     * - cus_1234567890 (customer ID)
     *
     * Si existe, se usa para guardar la tarjeta del cliente.
     */
    private String customerId;

    /**
     * Descripción del pago (opcional).
     *
     * Se usa para registros y facturas.
     *
     * Ejemplo:
     * "Consulta médica - 30 minutos"
     */
    private String description;
}

