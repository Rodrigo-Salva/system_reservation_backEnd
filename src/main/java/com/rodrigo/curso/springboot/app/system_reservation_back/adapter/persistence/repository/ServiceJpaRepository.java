package com.rodrigo.curso.springboot.app.system_reservation_back.adapter.persistence.repository;

import com.rodrigo.curso.springboot.app.system_reservation_back.adapter.persistence.entity.ServiceJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * SPRING DATA REPOSITORY para Service.
 */
@Repository
public interface ServiceJpaRepository extends JpaRepository<ServiceJpaEntity, UUID> {

    /**
     * Listar servicios activos.
     */
    List<ServiceJpaEntity> findByIsActiveTrue();

    /**
     * Listar servicios de un proveedor.
     */
    List<ServiceJpaEntity> findByProviderId(UUID providerId);
}
