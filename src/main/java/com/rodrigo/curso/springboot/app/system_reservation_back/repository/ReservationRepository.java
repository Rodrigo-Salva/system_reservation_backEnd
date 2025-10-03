package com.rodrigo.curso.springboot.app.system_reservation_back.repository;

import com.rodrigo.curso.springboot.app.system_reservation_back.entity.Hall;
import com.rodrigo.curso.springboot.app.system_reservation_back.entity.Reservation;
import com.rodrigo.curso.springboot.app.system_reservation_back.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByUser(User user);

    List<Reservation> findByHall(Hall hall);

    // Buscar reservas que se crucen en el mismo rango de tiempo
    List<Reservation> findByHallAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Hall hall,
            LocalDateTime endDate,
            LocalDateTime startDate
    );

}
