package com.rodrigo.curso.springboot.app.system_reservation_back.controller;

import com.rodrigo.curso.springboot.app.system_reservation_back.dto.ReservationDTO;
import com.rodrigo.curso.springboot.app.system_reservation_back.entity.Hall;
import com.rodrigo.curso.springboot.app.system_reservation_back.entity.Reservation;
import com.rodrigo.curso.springboot.app.system_reservation_back.entity.User;
import com.rodrigo.curso.springboot.app.system_reservation_back.service.HallService;
import com.rodrigo.curso.springboot.app.system_reservation_back.service.ReservationService;
import com.rodrigo.curso.springboot.app.system_reservation_back.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final UserService userService;
    private final HallService hallService;

    public ReservationController(ReservationService reservationService,
                                 UserService userService,
                                 HallService hallService) {
        this.reservationService = reservationService;
        this.userService = userService;
        this.hallService = hallService;
    }

    @GetMapping
    public List<Reservation> getAllReservations() {
        return reservationService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable Long id) {
        return reservationService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createReservation(@Valid @RequestBody ReservationDTO reservationDTO) {
        User user = userService.findById(reservationDTO.getUserId()).orElse(null);
        Hall hall = hallService.findById(reservationDTO.getHallId()).orElse(null);

        if (user == null || hall == null) {
            return ResponseEntity.badRequest().body("Usuario o sala no encontrados");
        }

        try {
            Reservation reservation = new Reservation();
            reservation.setUser(user);
            reservation.setHall(hall);
            reservation.setStartDate(reservationDTO.getStartDate());
            reservation.setEndDate(reservationDTO.getEndDate());
            reservation.setState(reservationDTO.getState());
            reservation.setGrades(reservationDTO.getGrades());

            return ResponseEntity.ok(reservationService.save(reservation));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        reservationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}