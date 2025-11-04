package com.rodrigo.curso.springboot.app.system_reservation_back.common.mapper;

import com.rodrigo.curso.springboot.app.system_reservation_back.adapter.persistence.entity.ReservationJpaEntity;
import com.rodrigo.curso.springboot.app.system_reservation_back.entity.Reservation;
import com.rodrigo.curso.springboot.app.system_reservation_back.entity.valuobject.DateRange;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * MAPPER: Convierte entre Reservation (Dominio) y ReservationJpaEntity (JPA)
 */
@Component
public class ReservationMapper {

    /**
     * CONVERTIR: ReservationJpaEntity → Reservation (Dominio)
     */
    public Reservation toDomain(ReservationJpaEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }

        // Reconstruir desde BD
        return Reservation.reconstructFromDatabase(
                jpaEntity.getId(),
                jpaEntity.getUserId(),
                jpaEntity.getServiceId(),
                new DateRange(
                        jpaEntity.getDate(),
                        jpaEntity.getStartTime(),
                        jpaEntity.getEndTime()
                ),
                jpaEntity.getStatus(),
                jpaEntity.getNotes(),
                jpaEntity.getCreatedAt()
        );
    }

    /**
     * CONVERTIR: Reservation (Dominio) → ReservationJpaEntity
     */
    public ReservationJpaEntity toEntity(Reservation domain) {
        if (domain == null) {
            return null;
        }

        return ReservationJpaEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .serviceId(domain.getServiceId())
                .date(domain.getDateRange().getDate())
                .startTime(domain.getDateRange().getStartTime())
                .endTime(domain.getDateRange().getEndTime())
                .status(domain.getStatus())
                .notes(domain.getNotes())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}

