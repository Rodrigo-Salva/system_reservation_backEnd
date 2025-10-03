package com.rodrigo.curso.springboot.app.system_reservation_back.repository;

import com.rodrigo.curso.springboot.app.system_reservation_back.entity.Hall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HallRepository extends JpaRepository<Hall, Integer> {
    boolean existsByName(String name);

    Optional<Hall> findById(Long id);
    void deleteById(Long id);
}
