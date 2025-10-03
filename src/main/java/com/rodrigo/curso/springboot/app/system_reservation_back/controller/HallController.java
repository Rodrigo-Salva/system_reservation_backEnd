package com.rodrigo.curso.springboot.app.system_reservation_back.controller;

import com.rodrigo.curso.springboot.app.system_reservation_back.dto.HallDTO;
import com.rodrigo.curso.springboot.app.system_reservation_back.entity.Hall;
import com.rodrigo.curso.springboot.app.system_reservation_back.service.HallService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/halls")
public class HallController {

    private final HallService hallService;

    public HallController(HallService hallService) {
        this.hallService = hallService;
    }

    @GetMapping
    public List<Hall> getAllHalls() {
        return hallService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Hall> getHallById(@PathVariable Long id) {
        return hallService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Hall> createHall(@Valid @RequestBody HallDTO hallDTO) {
        Hall hall = new Hall();
        hall.setName(hallDTO.getName());
        hall.setDescription(hallDTO.getDescription());
        hall.setCapacity(hallDTO.getCapacity());
        hall.setPricePerHour(hallDTO.getPricePerHour());
        hall.setLocation(hallDTO.getLocation());
        hall.setActive(true);

        return ResponseEntity.ok(hallService.save(hall));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHall(@PathVariable Long id) {
        hallService.delete(id);
        return ResponseEntity.noContent().build();
    }
}