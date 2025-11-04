package com.rodrigo.curso.springboot.app.system_reservation_back.entity.valuobject;

/**
 * ENUM que representa los ESTADOS POSIBLES de una reserva.
 *
 * ¿Por qué un ENUM?
 *
 * ❌ MAL - Con String:
 * String status = "PENDING";
 * if (status.equals("PENDNG")) {  // ¡TYPO! No se detecta
 *   // ...
 * }
 *
 * ✅ BIEN - Con Enum:
 * ReservationStatus status = ReservationStatus.PENDING;
 * if (status == ReservationStatus.PENDING) {  // El compilador valida
 *   // ...
 * }
 *
 * ESTADOS (RN-018):
 * PENDING    → Creada, esperando pago
 * CONFIRMED  → Pago procesado, confirmada
 * COMPLETED  → Servicio ejecutado
 * CANCELLED  → Cancelada
 */
public enum ReservationStatus {
    PENDING,
    CONFIRMED,
    COMPLETED,
    CANCELLED;

    /**
     * RN-019: VALIDA TRANSICIONES DE ESTADO
     *
     * Reglas permitidas:
     * PENDING   → CONFIRMED o CANCELLED
     * CONFIRMED → COMPLETED o CANCELLED
     * COMPLETED → (ninguno, estado final)
     * CANCELLED → (ninguno, estado final)
     *
     * ¿Por qué una máquina de estados?
     * Para evitar transiciones inválidas como:
     * COMPLETED → PENDING  (¡Imposible!)
     * CANCELLED → CONFIRMED  (¡Imposible!)
     *
     * @param target Estado al que quieres transicionar
     * @return true si la transición es permitida, false si no
     *
     * Ejemplo:
     * ReservationStatus.PENDING.canTransitionTo(CONFIRMED);  // → true
     * ReservationStatus.COMPLETED.canTransitionTo(CANCELLED); // → false
     */
    public boolean canTransitionTo(ReservationStatus target) {
        // Switch con arrow syntax (Java 14+)
        return switch (this) {
            // Desde PENDING
            case PENDING ->
                    target == CONFIRMED || target == CANCELLED;

            // Desde CONFIRMED
            case CONFIRMED ->
                    target == COMPLETED || target == CANCELLED;

            // COMPLETED y CANCELLED son estados finales
            case COMPLETED, CANCELLED ->
                    false;  // No se puede transicionar desde aquí
        };
    }
}
