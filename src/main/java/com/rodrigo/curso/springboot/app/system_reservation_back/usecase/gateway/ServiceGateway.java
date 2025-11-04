package com.rodrigo.curso.springboot.app.system_reservation_back.usecase.gateway;

import com.rodrigo.curso.springboot.app.system_reservation_back.entity.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * GATEWAY para acceder a servicios.
 */
public interface ServiceGateway {

    /**
     * Guarda o actualiza un servicio.
     */
    Service save(Service service);

    /**
     * Busca un servicio por ID.
     */
    Optional<Service> findById(UUID id);

    /**
     * Lista todos los servicios activos.
     */
    List<Service> findAllActive();

    /**
     * Lista servicios de un proveedor.
     */
    List<Service> findByProviderId(UUID providerId);

    /**
     * Verifica si un servicio existe y está activo.
     */
    boolean existsAndActive(UUID id);
}

