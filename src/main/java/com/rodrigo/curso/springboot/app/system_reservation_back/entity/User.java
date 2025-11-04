package com.rodrigo.curso.springboot.app.system_reservation_back.entity;

import com.rodrigo.curso.springboot.app.system_reservation_back.entity.exception.UserException;
import com.rodrigo.curso.springboot.app.system_reservation_back.entity.valuobject.Email;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * ENTIDAD DE DOMINIO: USER
 *
 * Responsabilidades:
 * 1. Mantener estado del usuario
 * 2. Validar reglas de negocio (contraseña, email, etc)
 * 3. Controlar transiciones de estado
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class User {

    private UUID id;
    private Email email;
    private String password;
    private String name;
    private String phone;
    private UserRole role;
    private UserStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ==========================
    // ENUMS
    // ==========================

    public enum UserRole {
        CLIENTE,
        PROVEEDOR,
        ADMIN
    }

    public enum UserStatus {
        INACTIVE,
        ACTIVE,
        SUSPENDED,
        DELETED
    }

    // ==========================
    // CONSTRUCTOR PRIVADO
    // ==========================
    private User(
            UUID id,
            Email email,
            String password,
            String name,
            String phone,
            UserRole role,
            UserStatus status,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.role = role;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
    }

    // ==========================
    // FACTORY METHODS
    // ==========================

    /**
     * Crear un nuevo usuario desde cero
     */
    public static User create(String email, String password, String name, String phone) {
        validatePassword(password);
        return new User(
                UUID.randomUUID(),
                new Email(email),
                password,
                name,
                phone,
                UserRole.CLIENTE,
                UserStatus.INACTIVE,
                LocalDateTime.now()
        );
    }

    /**
     * Reconstruir un usuario ya existente desde la base de datos.
     * Usado por los mappers (infraestructura/persistencia)
     */
    public static User reconstructFromDatabase(
            UUID id,
            String email,
            String password,
            String name,
            String phone,
            UserRole role,
            UserStatus status,
            LocalDateTime createdAt
    ) {
        User user = new User(
                id,
                new Email(email),
                password,
                name,
                phone,
                role,
                status,
                createdAt
        );
        user.updatedAt = LocalDateTime.now();
        return user;
    }

    // ==========================
    // VALIDACIONES
    // ==========================
    private static void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw UserException.invalidPassword();
        }
        if (!password.matches(".*[A-Z].*")) {
            throw UserException.invalidPassword();
        }
        if (!password.matches(".*[a-z].*")) {
            throw UserException.invalidPassword();
        }
        if (!password.matches(".*[0-9].*")) {
            throw UserException.invalidPassword();
        }
        if (!password.matches(".*[!@#$%^&*()_+=-].*")) {
            throw UserException.invalidPassword();
        }
    }

    // ==========================
    // OPERACIONES DE NEGOCIO
    // ==========================

    public void activate() {
        this.status = UserStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public void suspend() {
        this.status = UserStatus.SUSPENDED;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateProfile(String name, String phone) {
        this.name = name;
        this.phone = phone;
        this.updatedAt = LocalDateTime.now();
    }

    public void changeRole(UserRole newRole) {
        this.role = newRole;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }
}
