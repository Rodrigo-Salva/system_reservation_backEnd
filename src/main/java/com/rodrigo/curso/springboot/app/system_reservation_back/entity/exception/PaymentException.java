package com.rodrigo.curso.springboot.app.system_reservation_back.entity.exception;

/**
 * EXCEPCIONES ESPECÍFICAS relacionadas con PAGOS.
 */
public class PaymentException extends DomainException {

    public PaymentException(String message) {
        super(message);
    }

    /**
     * RN-027: Monto del pago válido
     *
     * Se lanza cuando el monto es <= 0 o null.
     *
     * Ejemplo:
     * - Precio del servicio: $50
     * - Intentas pagar: $0 ← ERROR
     * - Intentas pagar: -$10 ← ERROR
     */
    public static PaymentException invalidAmount() {
        return new PaymentException(
                "Payment amount must be greater than zero"
        );
    }

    /**
     * RN-028: Pago falló en el proveedor
     *
     * Se lanza cuando el procesador de pagos (Stripe, PayPal) rechaza el pago.
     *
     * Ejemplo:
     * - Tarjeta sin fondos
     * - Tarjeta expirada
     * - Error del banco
     */
    public static PaymentException processingFailed(String reason) {
        return new PaymentException(
                "Payment processing failed: " + reason
        );
    }

    /**
     * Se lanza cuando buscas un pago que no existe.
     */
    public static PaymentException notFound(String id) {
        return new PaymentException("Payment not found: " + id);
    }
}

