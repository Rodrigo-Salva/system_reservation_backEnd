package com.rodrigo.curso.springboot.app.system_reservation_back.adapter.persistence.entity;

import com.rodrigo.curso.springboot.app.system_reservation_back.common.mapper.UserMapper;
import com.rodrigo.curso.springboot.app.system_reservation_back.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * ENTIDAD JPA para persistir Usuario en BD.
 *
 * Mapea con la tabla: users
 */
@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_user_email", columnList = "email")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) // ✅ Genera el UUID automáticamente
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private User.UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private User.UserStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * CONVERTIR: JpaEntity → Entity de Dominio
     */
    public User toDomain() {
        return new UserMapper().toDomain(this);
    }

    /**
     * CONVERTIR: Entity de Dominio → JpaEntity
     */
    public static UserJpaEntity fromDomain(User domain) {
        return new UserMapper().toEntity(domain);
    }
}
