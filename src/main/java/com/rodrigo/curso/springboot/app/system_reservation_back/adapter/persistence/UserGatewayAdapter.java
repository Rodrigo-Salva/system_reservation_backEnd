package com.rodrigo.curso.springboot.app.system_reservation_back.adapter.persistence;

import com.rodrigo.curso.springboot.app.system_reservation_back.adapter.persistence.entity.UserJpaEntity;
import com.rodrigo.curso.springboot.app.system_reservation_back.adapter.persistence.repository.UserJpaRepository;
import com.rodrigo.curso.springboot.app.system_reservation_back.entity.User;
import com.rodrigo.curso.springboot.app.system_reservation_back.usecase.gateway.UserGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * ADAPTER: Implementación de UserGateway usando JPA.
 *
 * ¿Qué hace?
 * Convierte entre:
 * - Entity de Dominio (User)
 * - JPA Entity (UserJpaEntity)
 * - Y hace queries a la BD
 *
 * ¿Por qué @Repository?
 * Para que Spring lo registre como un bean
 * que implementa UserGateway.
 *
 * Ahora cuando el Use Case inyecta UserGateway,
 * Spring automáticamente inyecta UserGatewayAdapter.
 */
@Repository
@RequiredArgsConstructor
public class UserGatewayAdapter implements UserGateway {

    private final UserJpaRepository userJpaRepository;

    @Override
    public User save(User user) {
        // Convertir Entity de dominio → JPA Entity
        UserJpaEntity jpaEntity = UserJpaEntity.fromDomain(user);

        // Guardar en BD
        UserJpaEntity savedEntity = userJpaRepository.save(jpaEntity);

        // Convertir JPA Entity → Entity de dominio
        return savedEntity.toDomain();
    }

    @Override
    public Optional<User> findById(UUID id) {
        // Buscar en BD
        Optional<UserJpaEntity> entity = userJpaRepository.findById(id);

        // Convertir JPA Entity → Entity de dominio
        return entity.map(UserJpaEntity::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        // Buscar en BD
        Optional<UserJpaEntity> entity = userJpaRepository.findByEmail(email);

        // Convertir
        return entity.map(UserJpaEntity::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    public void delete(UUID id) {
        userJpaRepository.deleteById(id);
    }
}
