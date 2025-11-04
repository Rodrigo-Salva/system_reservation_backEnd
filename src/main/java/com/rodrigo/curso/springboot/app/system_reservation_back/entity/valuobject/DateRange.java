package com.rodrigo.curso.springboot.app.system_reservation_back.entity.valuobject;

import com.rodrigo.curso.springboot.app.system_reservation_back.entity.exception.ReservationException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

/**
 * VALUE OBJECT que representa un RANGO DE FECHA Y HORA.
 *
 * ¿Por qué no pasar date, startTime, endTime por separado?
 *
 * ❌ MAL:
 * createReservation(date, startTime, endTime)
 * // ¿Se valida que endTime > startTime? No sabemos
 * // ¿Se valida que sea una fecha válida? No sabemos
 *
 * ✅ BIEN:
 * DateRange range = new DateRange(date, startTime, endTime);  // Valida aquí
 * createReservation(range)
 * // Sabemos que range es SIEMPRE válido
 *
 * CONCEPTOS:
 * - LocalDate: Solo la fecha (2025-11-02)
 * - LocalTime: Solo la hora (14:30)
 * - LocalDateTime: Fecha + hora (2025-11-02 14:30)
 */
public class DateRange {

    private final LocalDate date;          // La fecha (ej: 2025-11-02)
    private final LocalTime startTime;     // Hora de inicio (ej: 14:00)
    private final LocalTime endTime;       // Hora de fin (ej: 15:00)

    /**
     * CONSTRUCTOR - Valida el rango de fecha/hora
     *
     * @param date La fecha
     * @param startTime Hora de inicio
     * @param endTime Hora de fin
     * @throws ReservationException si alguno es null o si endTime <= startTime
     */
    public DateRange(LocalDate date, LocalTime startTime, LocalTime endTime) {
        // Validación 1: Ninguno puede ser null
        if (date == null || startTime == null || endTime == null) {
            throw new ReservationException("Date and times cannot be null");
        }

        // Validación 2: End time DEBE ser DESPUÉS de start time
        // ¿Por qué isAfter y no isAfterOrEqual?
        // Porque una reserva no puede tener duración 0
        if (!endTime.isAfter(startTime)) {
            throw new ReservationException("End time must be after start time");
        }

        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // GETTERS
    public LocalDate getDate() {
        return date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    /**
     * Convierte a LocalDateTime (fecha + hora juntas)
     *
     * Ejemplo:
     * DateRange range = new DateRange(
     *     LocalDate.of(2025, 11, 2),
     *     LocalTime.of(14, 0),
     *     LocalTime.of(15, 0)
     * );
     * range.getStartDateTime();  // → 2025-11-02T14:00
     */
    public LocalDateTime getStartDateTime() {
        return LocalDateTime.of(date, startTime);
    }

    public LocalDateTime getEndDateTime() {
        return LocalDateTime.of(date, endTime);
    }

    /**
     * RN-015: DETECCIÓN DE SOLAPAMIENTO
     *
     * ¿Se solapa este rango con otro?
     *
     * LÓGICA:
     * Dos rangos se solapan si:
     * 1. Están en la MISMA fecha
     * 2. start1 < end2  AND  end1 > start2
     *
     * EJEMPLOS:
     * Rango 1: 14:00 - 15:00
     * Rango 2: 14:30 - 15:30
     * ¿Se solapan?
     * - 14:00 < 15:30? ✅ Sí
     * - 15:00 > 14:30? ✅ Sí
     * → SOLAPAN (desde 14:30 a 15:00)
     *
     * Rango 1: 14:00 - 15:00
     * Rango 2: 15:00 - 16:00
     * ¿Se solapan?
     * - 14:00 < 16:00? ✅ Sí
     * - 15:00 > 15:00? ❌ No
     * → NO SOLAPAN (son consecutivos, no exacto)
     *
     * ¿Por qué es importante?
     * Para RN-015: Prevención de doble reserva en el mismo servicio
     */
    public boolean overlaps(DateRange other) {
        // Si están en diferentes fechas, no se solapan
        if (!this.date.equals(other.date)) {
            return false;
        }

        // Fórmula de solapamiento de rangos
        return this.startTime.isBefore(other.endTime) &&
                this.endTime.isAfter(other.startTime);
    }

    /**
     * RN-016 y RN-017: VALIDA ANTICIPACIÓN MÍNIMA
     *
     * ¿La reserva es dentro de 24 horas?
     *
     * Ejemplo:
     * - Hoy es: 2025-11-02 10:00
     * - Intentas reservar para: 2025-11-03 09:00
     * - Diferencia: 23 horas ← DENTRO DE 24 HORAS (ERROR)
     * - Intentas reservar para: 2025-11-03 11:00
     * - Diferencia: 25 horas ← FUERA DE 24 HORAS (OK)
     */
    public boolean isWithin24Hours() {
        LocalDateTime start = this.getStartDateTime();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime plusOneDay = now.plusHours(24);

        // ¿Está el inicio antes de 24 horas desde ahora?
        return start.isBefore(plusOneDay);
    }

    /**
     * ¿La reserva es en el pasado?
     *
     * Ejemplo:
     * - Hoy es: 2025-11-02 10:00
     * - Intentas reservar para: 2025-11-02 09:00 ← PASADO (ERROR)
     */
    public boolean isPast() {
        return this.getStartDateTime().isBefore(LocalDateTime.now());
    }

    // EQUALS y HASHCODE
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DateRange dateRange = (DateRange) o;
        return Objects.equals(date, dateRange.date) &&
                Objects.equals(startTime, dateRange.startTime) &&
                Objects.equals(endTime, dateRange.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, startTime, endTime);
    }
}
