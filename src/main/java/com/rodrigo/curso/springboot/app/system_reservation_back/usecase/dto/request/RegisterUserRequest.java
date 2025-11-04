package com.rodrigo.curso.springboot.app.system_reservation_back.usecase.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para registrar un nuevo usuario.
 *
 * ¿Por qué anotaciones de validación?
 * Para validar ANTES de llegar al Use Case.
 * Spring valida automáticamente con @Valid.
 *
 * ¿Por qué Lombok (@Getter, @Setter)?
 * Para no escribir getters/setters manualmente.
 * Lombok los genera automáticamente.
 */
@Getter
@Setter
@NoArgsConstructor  // Constructor sin argumentos (para Jackson JSON)
@AllArgsConstructor // Constructor con todos los argumentos
public class RegisterUserRequest {


    /**
     * Email del usuario.
     *
     * @NotBlank: No puede ser null ni vacío
     * @Email: Debe tener formato de email
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    /**
     * Contraseña.
     *
     * @NotBlank: No puede ser null ni vacío
     * @Size: Debe tener mínimo 8 caracteres
     * @Pattern: Debe cumplir la regex (mayúscula, número, símbolo)
     *
     * La regex explicada:
     * (?=.*[A-Z])     → Al menos 1 mayúscula
     * (?=.*[a-z])     → Al menos 1 minúscula
     * (?=.*[0-9])     → Al menos 1 número
     * (?=.*[!@#$%...]) → Al menos 1 símbolo
     * .{8,}           → Mínimo 8 caracteres
     */
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[!@#$%^&*()_+=-]).{8,}$",
            message = "Password must contain uppercase, lowercase, number and special character"
    )
    private String password;

    /**
     * Nombre completo.
     */
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    /**
     * Teléfono.
     */
    @NotBlank(message = "Phone is required")
    @Pattern(
            regexp = "^[0-9]{9,15}$",
            message = "Phone must be between 9 and 15 digits"
    )
    private String phone;
}
