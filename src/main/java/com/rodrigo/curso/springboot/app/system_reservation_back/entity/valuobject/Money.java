package com.rodrigo.curso.springboot.app.system_reservation_back.entity.valuobject;


import com.rodrigo.curso.springboot.app.system_reservation_back.entity.exception.PaymentException;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * VALUE OBJECT que representa DINERO en el sistema.
 *
 * ¿Por qué NO usar solo BigDecimal?
 *
 * Porque el dinero tiene DOS conceptos:
 * 1. Cantidad (amount)
 * 2. Moneda (currency)
 *
 * ❌ Con BigDecimal:
 * BigDecimal price = new BigDecimal("50.00");  // ¿En qué moneda? No sabemos
 *
 * ✅ Con Money:
 * Money price = new Money(new BigDecimal("50.00"), "USD");  // Claro: 50 USD
 *
 * ¿Por qué BigDecimal y no double?
 * double tiene errores de precisión:
 * double x = 0.1 + 0.2;  // → 0.30000000000000004 ❌
 * BigDecimal es EXACTO para dinero
 */
public class Money {

    private final BigDecimal amount;   // Cantidad
    private final String currency;     // Moneda (USD, EUR, etc)

    /**
     * CONSTRUCTOR - Valida y crea el dinero
     *
     * @param amount Cantidad de dinero
     * @param currency Código de moneda (USD, EUR, etc)
     * @throws PaymentException si el monto es inválido
     */
    public Money(BigDecimal amount, String currency) {
        // Validación: Monto debe ser > 0
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw PaymentException.invalidAmount();
        }

        this.amount = amount;
        // Si no especifican moneda, por defecto USD
        this.currency = currency != null ? currency : "USD";
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        // Dos Money son iguales si tienen mismo amount Y currency
        return Objects.equals(amount, money.amount) &&
                Objects.equals(currency, money.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }

    @Override
    public String toString() {
        return amount + " " + currency;  // "50.00 USD"
    }
}

