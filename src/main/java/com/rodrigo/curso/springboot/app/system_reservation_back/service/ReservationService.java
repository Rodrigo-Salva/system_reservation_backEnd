package com.rodrigo.curso.springboot.app.system_reservation_back.service;

import com.rodrigo.curso.springboot.app.system_reservation_back.entity.Hall;
import com.rodrigo.curso.springboot.app.system_reservation_back.entity.Reservation;
import com.rodrigo.curso.springboot.app.system_reservation_back.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    public Optional<Reservation> findById(Long id) {
        return reservationRepository.findById(id);
    }

    public Reservation save(Reservation reservation) {
        // Validar solapamientos
        if (isHallOccupied(reservation.getHall(), reservation.getStartDate(), reservation.getEndDate())) {
            throw new IllegalStateException("La sala ya está reservada en ese horario");
        }

        return reservationRepository.save(reservation);
    }

    public void delete(Long id) {
        reservationRepository.deleteById(id);
    }

    // 🔥 Valida que no haya otra reserva que se cruce en el mismo rango de fechas
    private boolean isHallOccupied(Hall hall, LocalDateTime start, LocalDateTime end) {
        List<Reservation> overlappingReservations =
                reservationRepository.findByHallAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        hall, end, start
                );
        return !overlappingReservations.isEmpty();
    }
}