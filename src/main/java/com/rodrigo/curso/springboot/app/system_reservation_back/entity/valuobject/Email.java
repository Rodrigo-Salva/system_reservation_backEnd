package com.rodrigo.curso.springboot.app.system_reservation_back.entity.valuobject;


import com.rodrigo.curso.springboot.app.system_reservation_back.entity.exception.UserException;

import java.util.Objects;

/**
 * VALUE OBJECT que representa un EMAIL en el sistema.
 *
 * ¿Por qué NO usar solo String?
 *
 * ❌ Con String:
 * String email = "no-es-email";  // ¿Válido? No se valida aquí
 * saveUser(email);  // Error después
 *
 * ✅ Con Value Object:
 * Email email = new Email("no-es-email");  // ¡Error INMEDIATAMENTE!
 *
 * Beneficios:
 * 1. Validación en construcción
 * 2. Lógica centralizada
 * 3. Imposible tener un Email inválido en el sistema
 */
public class Email {

    // final = inmutable (no se puede cambiar después de creación)
    private final String value;

    /**
     * CONSTRUCTOR - Valida el email al crearlo
     *
     * @param value El string del email
     * @throws UserException si el email es inválido
     */
    public Email(String value) {
        // Validación 1: No null, no vacío
        if (value == null || value.isBlank()) {
            throw UserException.invalidEmail(value);
        }

        // Validación 2: Formato correcto
        if (!isValid(value)) {
            throw UserException.invalidEmail(value);
        }

        // Normalización: Convertir a minúsculas
        // ¿Por qué? Para que "User@Test.com" y "user@test.com" sean iguales
        this.value = value.toLowerCase();
    }

    /**
     * VALIDA el formato del email usando REGEX
     *
     * Regex explicado:
     * ^                    → Inicio del string
     * [A-Za-z0-9._%+-]+   → Uno o más caracteres válidos antes del @
     * @                    → El símbolo @
     * [A-Za-z0-9.-]+      → Uno o más caracteres del dominio
     * \\.                  → Un punto literal
     * [A-Z|a-z]{2,}       → Extensión de 2+ letras (.com, .es, .org)
     * $                    → Fin del string
     *
     * Ejemplos:
     * ✅ "user@test.com"      → válido
     * ✅ "user.name@test.com" → válido
     * ❌ "user@test"          → inválido (sin extensión)
     * ❌ "user.test.com"      → inválido (sin @)
     */
    private static boolean isValid(String email) {
        return email.matches(
                "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$"
        );
    }

    // Getter
    public String getValue() {
        return value;
    }

    /**
     * EQUALS - Para comparar dos Emails
     *
     * ¿Por qué necesitamos esto?
     * Para poder hacer:
     * Email email1 = new Email("test@test.com");
     * Email email2 = new Email("test@test.com");
     * email1.equals(email2);  // → true
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;  // Misma instancia
        if (o == null || getClass() != o.getClass()) return false;  // Tipo diferente
        Email email = (Email) o;
        return Objects.equals(value, email.value);  // Compara el valor
    }

    /**
     * HASHCODE - Para poder usar Email en HashMaps/HashSets
     *
     * Regla: Si equals() usa 'value', hashCode() también debe usarlo
     */
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    /**
     * TOSTRING - Para imprimir el email fácilmente
     */
    @Override
    public String toString() {
        return value;
    }
}
