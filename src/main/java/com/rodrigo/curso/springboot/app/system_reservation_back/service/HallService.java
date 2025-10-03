package com.rodrigo.curso.springboot.app.system_reservation_back.service;

import com.rodrigo.curso.springboot.app.system_reservation_back.entity.Hall;
import com.rodrigo.curso.springboot.app.system_reservation_back.repository.HallRepository;
import com.rodrigo.curso.springboot.app.system_reservation_back.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HallService {

    private final HallRepository hallRepository;

    public HallService(HallRepository hallRepository) {
        this.hallRepository = hallRepository;
    }

    public List<Hall> findAll() {
        return hallRepository.findAll();
    }

    public Optional<Hall> findById(Long id) {
        return hallRepository.findById(id);
    }

    public Hall save(Hall hall) {
        if (hallRepository.existsByName(hall.getName())) {
            throw new RuntimeException("Ya existe una sala con ese nombre");
        }
        return hallRepository.save(hall);
    }

    public void delete(Long id) {
        hallRepository.deleteById(id);
    }
}
