package com.rodrigo.curso.springboot.app.system_reservation_back.common.mapper;

import com.rodrigo.curso.springboot.app.system_reservation_back.adapter.persistence.entity.ServiceJpaEntity;
import com.rodrigo.curso.springboot.app.system_reservation_back.entity.Service;
import com.rodrigo.curso.springboot.app.system_reservation_back.entity.valuobject.Money;
import org.springframework.stereotype.Component;

/**
 * MAPPER: Convierte entre Service (Dominio) y ServiceJpaEntity (JPA)
 */
@Component
public class ServiceMapper {

    public Service toDomain(ServiceJpaEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }

        return Service.reconstructFromDatabase(
                jpaEntity.getId(),
                jpaEntity.getProviderId(),
                jpaEntity.getName(),
                jpaEntity.getDescription(),
                jpaEntity.getDurationMinutes(),
                new Money(jpaEntity.getPrice(), "USD"),
                jpaEntity.getMaxConcurrentReservations(),
                jpaEntity.getIsActive(),
                jpaEntity.getCreatedAt()
        );
    }

    public ServiceJpaEntity toEntity(Service domain) {
        if (domain == null) {
            return null;
        }

        return ServiceJpaEntity.builder()
                .id(domain.getId())
                .providerId(domain.getProviderId())
                .name(domain.getName())
                .description(domain.getDescription())
                .durationMinutes(domain.getDurationMinutes())
                .price(domain.getPrice().getAmount())
                .maxConcurrentReservations(domain.getMaxConcurrentReservations())
                .isActive(domain.isAvailable())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}
