package com.rodrigo.curso.springboot.app.system_reservation_back.repository;

import com.rodrigo.curso.springboot.app.system_reservation_back.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User,Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
