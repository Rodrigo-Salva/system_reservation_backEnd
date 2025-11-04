package com.rodrigo.curso.springboot.app.system_reservation_back.common.mapper;

import com.rodrigo.curso.springboot.app.system_reservation_back.adapter.persistence.entity.UserJpaEntity;
import com.rodrigo.curso.springboot.app.system_reservation_back.entity.User;
import org.springframework.stereotype.Component;

/**
 * MAPPER: Convierte entre User (Dominio) y UserJpaEntity (JPA)
 */
@Component
public class UserMapper {

    /**
     * CONVERTIR: UserJpaEntity → User (Dominio)
     */
    public User toDomain(UserJpaEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }

        // Usar el factory method para reconstruir desde BD
        return User.reconstructFromDatabase(
                jpaEntity.getId(),
                jpaEntity.getEmail(),
                jpaEntity.getPassword(),
                jpaEntity.getName(),
                jpaEntity.getPhone(),
                jpaEntity.getRole(),
                jpaEntity.getStatus(),
                jpaEntity.getCreatedAt()
        );
    }

    /**
     * CONVERTIR: User (Dominio) → UserJpaEntity
     */
    public UserJpaEntity toEntity(User domain) {
        if (domain == null) {
            return null;
        }

        return UserJpaEntity.builder()
                .id(domain.getId())
                .email(domain.getEmail().getValue())
                .password(domain.getPassword())
                .name(domain.getName())
                .phone(domain.getPhone())
                .role(domain.getRole())
                .status(domain.getStatus())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}
