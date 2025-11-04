package com.rodrigo.curso.springboot.app.system_reservation_back.adapter.persistence;

import com.rodrigo.curso.springboot.app.system_reservation_back.adapter.persistence.entity.ReservationJpaEntity;
import com.rodrigo.curso.springboot.app.system_reservation_back.adapter.persistence.repository.ReservationJpaRepository;
import com.rodrigo.curso.springboot.app.system_reservation_back.entity.Reservation;
import com.rodrigo.curso.springboot.app.system_reservation_back.usecase.gateway.ReservationGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * ADAPTER: Implementación de ReservationGateway usando JPA.
 */
@Repository
@RequiredArgsConstructor
public class ReservationGatewayAdapter implements ReservationGateway {

    private final ReservationJpaRepository reservationJpaRepository;

    @Override
    public Reservation save(Reservation reservation) {
        ReservationJpaEntity jpaEntity = ReservationJpaEntity.fromDomain(reservation);
        ReservationJpaEntity savedEntity = reservationJpaRepository.save(jpaEntity);
        return savedEntity.toDomain();
    }

    @Override
    public Optional<Reservation> findById(UUID id) {
        return reservationJpaRepository.findById(id)
                .map(ReservationJpaEntity::toDomain);
    }

    @Override
    public List<Reservation> findOverlappingReservations(
            UUID serviceId,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime
    ) {
        return reservationJpaRepository.findOverlappingReservations(
                        serviceId, date, startTime, endTime)
                .stream()
                .map(ReservationJpaEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long countActiveReservationsByUser(UUID userId) {
        return reservationJpaRepository.countActiveReservationsByUser(userId);
    }

    @Override
    public long countReservationsForServiceInWeek(
            UUID userId,
            UUID serviceId,
            LocalDate startOfWeek,
            LocalDate endOfWeek
    ) {
        return reservationJpaRepository.countReservationsForServiceInWeek(
                userId, serviceId, startOfWeek, endOfWeek);
    }

    @Override
    public List<Reservation> findByUserId(UUID userId) {
        return reservationJpaRepository.findByUserId(userId)
                .stream()
                .map(ReservationJpaEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> findExpiredPendingReservations() {
        LocalDateTime expirationTime = LocalDateTime.now().minusHours(24);
        return reservationJpaRepository.findExpiredPendingReservations(expirationTime)
                .stream()
                .map(ReservationJpaEntity::toDomain)
                .collect(Collectors.toList());
    }
}

