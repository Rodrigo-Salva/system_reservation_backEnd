package com.rodrigo.curso.springboot.app.system_reservation_back.adapter.persistence;

import com.rodrigo.curso.springboot.app.system_reservation_back.adapter.persistence.entity.ServiceJpaEntity;
import com.rodrigo.curso.springboot.app.system_reservation_back.adapter.persistence.repository.ServiceJpaRepository;
import com.rodrigo.curso.springboot.app.system_reservation_back.common.mapper.ServiceMapper;
import com.rodrigo.curso.springboot.app.system_reservation_back.entity.Service;
import com.rodrigo.curso.springboot.app.system_reservation_back.usecase.gateway.ServiceGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * ADAPTER: Implementación de ServiceGateway usando JPA.
 */
@Repository
@RequiredArgsConstructor
public class ServiceGatewayAdapter implements ServiceGateway {

    private final ServiceJpaRepository serviceJpaRepository;
    private final ServiceMapper serviceMapper;

    @Override
    public Service save(Service service) {
        ServiceJpaEntity jpaEntity = serviceMapper.toEntity(service);
        ServiceJpaEntity savedEntity = serviceJpaRepository.save(jpaEntity);
        return serviceMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Service> findById(UUID id) {
        return serviceJpaRepository.findById(id)
                .map(serviceMapper::toDomain);
    }

    @Override
    public List<Service> findAllActive() {
        return serviceJpaRepository.findByIsActiveTrue()
                .stream()
                .map(serviceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Service> findByProviderId(UUID providerId) {
        return serviceJpaRepository.findByProviderId(providerId)
                .stream()
                .map(serviceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsAndActive(UUID id) {
        return serviceJpaRepository.findById(id)
                .map(serviceMapper::toDomain)  // <-- convertir a entidad de dominio
                .map(Service::isActive)         // <-- luego llamar al método
                .orElse(false);
    }
}