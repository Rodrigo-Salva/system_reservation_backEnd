package com.rodrigo.curso.springboot.app.system_reservation_back.usecase.dto;

import com.rodrigo.curso.springboot.app.system_reservation_back.entity.User;
import com.rodrigo.curso.springboot.app.system_reservation_back.entity.exception.UserException;
import com.rodrigo.curso.springboot.app.system_reservation_back.usecase.dto.request.RegisterUserRequest;
import com.rodrigo.curso.springboot.app.system_reservation_back.usecase.dto.response.UserResponse;
import com.rodrigo.curso.springboot.app.system_reservation_back.usecase.gateway.UserGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * USE CASE: Registrar un nuevo usuario.
 *
 * RESPONSABILIDADES:
 * 1. Validar que el email no exista (RN-001)
 * 2. Crear entidad User con validaciones (RN-002)
 * 3. Encriptar la contraseña
 * 4. Persistir en BD
 * 5. Retornar DTO de respuesta
 *
 * ¿Por qué @Service?
 * Es un componente de Spring que se puede inyectar.
 *
 * ¿Por qué @RequiredArgsConstructor de Lombok?
 * Genera un constructor con todos los campos "final".
 * Esto es inyección de dependencias por constructor (mejor práctica).
 *
 * ¿Por qué @Transactional?
 * Para que todo se haga en una transacción de BD.
 * Si algo falla, se hace rollback automático.
 */
@Service
@RequiredArgsConstructor
public class RegisterUserUseCase {

    // DEPENDENCIAS (se inyectan por constructor)
    private final UserGateway userGateway;
    private final PasswordEncoder passwordEncoder;

    /**
     * EJECUTA el caso de uso.
     *
     * @param request DTO con datos del usuario
     * @return DTO con el usuario creado
     * @throws UserException si el email ya existe o datos inválidos
     *
     * Ejemplo de uso (desde el Controller):
     * RegisterUserRequest request = new RegisterUserRequest(
     *     "user@test.com",
     *     "SecurePass123!",
     *     "Juan Pérez",
     *     "987654321"
     * );
     * UserResponse response = registerUserUseCase.execute(request);
     */
    @Transactional  // Todo en una transacción
    public UserResponse execute(RegisterUserRequest request) {

        // PASO 1: RN-001 - Validar que el email no exista
        if (userGateway.existsByEmail(request.getEmail())) {
            throw UserException.emailAlreadyExists(request.getEmail());
        }

        // PASO 2: Encriptar la contraseña
        // ¿Por qué encriptar?
        // NUNCA guardes contraseñas en texto plano.
        // BCrypt es un algoritmo de hash seguro (con salt).
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // PASO 3: RN-002 - Crear entidad User (validaciones dentro)
        // El factory method User.create() valida:
        // - Email válido (en constructor de Email value object)
        // - Contraseña segura (8+ chars, mayúscula, número, símbolo)
        User user = User.create(
                request.getEmail(),
                hashedPassword,  // Contraseña ya encriptada
                request.getName(),
                request.getPhone()
        );

        // PASO 4: Persistir en BD
        User savedUser = userGateway.save(user);

        // PASO 5: Convertir Entity a DTO de respuesta
        // ¿Por qué no retornar la Entity directamente?
        // - Security: No expones campos internos
        // - Desacoplamiento: El Controller no depende de la Entity
        return mapToResponse(savedUser);
    }

    /**
     * MAPPER: Convierte Entity User a DTO UserResponse
     *
     * ¿Por qué un método separado?
     * - Código más limpio
     * - Reutilizable
     * - Fácil de testear
     *
     * Nota: Más adelante podemos usar MapStruct para esto.
     */
    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail().getValue())  // Email es Value Object
                .name(user.getName())
                .phone(user.getPhone())
                .role(user.getRole().name())        // Enum a String
                .status(user.getStatus().name())
                .createdAt(user.getCreatedAt())
                .build();
    }
}

