package com.rodrigo.curso.springboot.app.system_reservation_back.adapter.persistence.repository;

import com.rodrigo.curso.springboot.app.system_reservation_back.adapter.persistence.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * SPRING DATA REPOSITORY para User.
 *
 * ¿Qué es JpaRepository?
 * Es una interfaz de Spring que genera SQL automáticamente.
 *
 * Heredas de JpaRepository<Entidad, TipoId> y obtienes:
 * - save(entity)
 * - findById(id)
 * - findAll()
 * - delete(entity)
 * - Y muchos más...
 *
 * ¿Por qué @Repository?
 * Para que Spring lo reconozca como un componente.
 */
@Repository
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, UUID> {

    /**
     * Query personalizado: Buscar por email.
     *
     * Spring genera el SQL automáticamente basado en el nombre:
     * Optional<UserJpaEntity> findByEmail(String email);
     * ↓
     * SELECT * FROM users WHERE email = ?
     */
    Optional<UserJpaEntity> findByEmail(String email);

    /**
     * Query personalizado: Verificar si existe email.
     *
     * Boolean existsByEmail(String email);
     * ↓
     * SELECT COUNT(*) > 0 FROM users WHERE email = ?
     */
    boolean existsByEmail(String email);
}

