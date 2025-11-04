package com.rodrigo.curso.springboot.app.system_reservation_back.usecase.gateway;

import com.rodrigo.curso.springboot.app.system_reservation_back.entity.User;

import java.util.Optional;
import java.util.UUID;

/**
 * GATEWAY (INTERFAZ) para acceder a usuarios.
 *
 * Define el CONTRATO de lo que el Use Case necesita.
 * La IMPLEMENTACIÓN se hará en la capa de Adapter.
 *
 * ¿Por qué interfaz?
 * Para invertir la dependencia:
 * - Use Case (dominio) define QUÉ necesita
 * - Adapter (infraestructura) implementa CÓMO lo hace
 */
public interface UserGateway {

    /**
     * Guarda o actualiza un usuario.
     *
     * @param user Usuario a guardar
     * @return Usuario guardado (con ID si es nuevo)
     */
    User save(User user);

    /**
     * Busca un usuario por ID.
     *
     * @param id ID del usuario
     * @return Optional con el usuario si existe
     */
    Optional<User> findById(UUID id);

    /**
     * Busca un usuario por email.
     *
     * @param email Email del usuario
     * @return Optional con el usuario si existe
     */
    Optional<User> findByEmail(String email);

    /**
     * Verifica si un email ya existe.
     *
     * @param email Email a verificar
     * @return true si existe, false si no
     */
    boolean existsByEmail(String email);

    /**
     * Elimina un usuario (soft delete).
     *
     * @param id ID del usuario a eliminar
     */
    void delete(UUID id);
}

