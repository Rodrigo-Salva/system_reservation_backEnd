package com.rodrigo.curso.springboot.app.system_reservation_back.usecase.dto;

import com.rodrigo.curso.springboot.app.system_reservation_back.entity.Payment;
import com.rodrigo.curso.springboot.app.system_reservation_back.entity.Reservation;
import com.rodrigo.curso.springboot.app.system_reservation_back.entity.exception.PaymentException;
import com.rodrigo.curso.springboot.app.system_reservation_back.entity.exception.ReservationException;
import com.rodrigo.curso.springboot.app.system_reservation_back.entity.valuobject.Money;
import com.rodrigo.curso.springboot.app.system_reservation_back.usecase.dto.request.ProcessPaymentRequest;
import com.rodrigo.curso.springboot.app.system_reservation_back.usecase.dto.response.PaymentResponse;
import com.rodrigo.curso.springboot.app.system_reservation_back.usecase.gateway.PaymentGateway;
import com.rodrigo.curso.springboot.app.system_reservation_back.usecase.gateway.ReservationGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * USE CASE: Procesar pago de una reserva.
 *
 * RESPONSABILIDADES:
 * 1. Validar que la reserva existe
 * 2. Validar que está en estado PENDING (no pagada)
 * 3. Crear entidad Payment
 * 4. Llamar a proveedor de pago (Stripe, PayPal)
 * 5. RN-028: Si éxito, marcar como COMPLETED
 * 6. RN-028: Si falla, marcar como FAILED
 * 7. RN-029: Cambiar Reservation a CONFIRMED si pago éxito
 * 8. Persistir cambios
 * 9. Retornar DTO
 *
 * IMPORTANTE: Este es un caso de uso ATÓMICO.
 * Si algo falla, TODO se revierte (rollback de transacción).
 */
@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ProcessPaymentUseCase {

    // DEPENDENCIAS
    private final ReservationGateway reservationGateway;
    private final PaymentGateway paymentGateway;
    // En producción, aquí habría un PaymentProviderAdapter
    // private final PaymentProviderAdapter paymentProvider;

    /**
     * EJECUTA el caso de uso.
     *
     * @param reservationId ID de la reserva
     * @param request DTO con datos de pago
     * @return DTO con resultado del pago
     * @throws ReservationException si la reserva no existe
     * @throws PaymentException si el pago falla
     */
    @Transactional
    public PaymentResponse execute(
            UUID reservationId,
            ProcessPaymentRequest request
    ) {

        // PASO 1: Buscar la reserva
        Reservation reservation = reservationGateway.findById(reservationId)
                .orElseThrow(() -> ReservationException.notFound(reservationId.toString()));

        // PASO 2: Validar que NO está ya pagada
        if (!reservation.isPending()) {
            throw new ReservationException(
                    "Cannot process payment for reservation in " +
                            reservation.getStatus() + " status"
            );
        }

        // PASO 3: Obtener precio del servicio (aquí simplificado)
        // En producción, buscarías el servicio para obtener el precio
        Money amount = new Money(request.getAmount(), "USD");

        // PASO 4: Crear entidad Payment (PENDING)
        Payment payment = Payment.create(
                reservationId,
                amount,
                request.getPaymentMethod()
        );

        // PASO 5: Guardar en BD (estado PENDING)
        Payment savedPayment = paymentGateway.save(payment);

        // PASO 6: LLAMAR A PROVEEDOR DE PAGO
        // Esto es donde la magia ocurre - comunicar con Stripe/PayPal
        try {
            PaymentProviderResponse providerResponse = callPaymentProvider(
                    savedPayment,
                    request
            );

            // PASO 7: RN-028 - Si éxito
            if (providerResponse.isSuccess()) {
                // Marcar pago como COMPLETED
                savedPayment.markAsCompleted(providerResponse.getTransactionId());

                // RN-029: Cambiar reserva a CONFIRMED (transacción atómica)
                reservation.confirm();

                // Persistir cambios
                paymentGateway.save(savedPayment);
                reservationGateway.save(reservation);

                return mapToResponse(savedPayment);
            } else {
                // PASO 8: RN-028 - Si falla
                savedPayment.markAsFailed(providerResponse.getErrorMessage());
                paymentGateway.save(savedPayment);

                throw PaymentException.processingFailed(
                        providerResponse.getErrorMessage()
                );
            }

        } catch (Exception e) {
            // Si hay error, marcar como FAILED
            savedPayment.markAsFailed(e.getMessage());
            paymentGateway.save(savedPayment);

            throw PaymentException.processingFailed(e.getMessage());
        }
    }

    /**
     * LLAMAR A PROVEEDOR DE PAGO (Simulado)
     *
     * En producción, esto haría una llamada real a Stripe API.
     *
     * Ejemplo con Stripe:
     * PaymentIntent intent = stripe.paymentIntents.create(
     *     params: {
     *         amount: 5000,
     *         currency: "usd",
     *         payment_method: request.paymentMethodId(),
     *         confirm: true
     *     }
     * );
     */
    private PaymentProviderResponse callPaymentProvider(
            Payment payment,
            ProcessPaymentRequest request
    ) {
        // TODO: Implementar en adapter

        // Por ahora, simulamos:
        // 80% de las veces éxito
        // 20% de las veces falla

        if (Math.random() > 0.2) {
            // Éxito
            return new PaymentProviderResponse(
                    true,
                    "txn_" + UUID.randomUUID().toString().substring(0, 10),
                    null
            );
        } else {
            // Falla
            return new PaymentProviderResponse(
                    false,
                    null,
                    "Card declined"
            );
        }
    }

    /**
     * MAPPER: Convierte Entity a DTO
     */
    private PaymentResponse mapToResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .reservationId(payment.getReservationId())
                .amount(payment.getAmount().getAmount())
                .currency(payment.getAmount().getCurrency())
                .status(payment.getStatus().name())
                .transactionId(payment.getTransactionId())
                .errorMessage(payment.getErrorMessage())
                .processedAt(payment.getProcessedAt())
                .build();
    }

    /**
     * INNER CLASS: Respuesta del proveedor de pago
     *
     * En producción, esta sería la respuesta de Stripe API.
     */
    private static class PaymentProviderResponse {
        private final boolean success;
        private final String transactionId;
        private final String errorMessage;

        PaymentProviderResponse(boolean success, String transactionId, String errorMessage) {
            this.success = success;
            this.transactionId = transactionId;
            this.errorMessage = errorMessage;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getTransactionId() {
            return transactionId;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}

