package com.rodrigo.curso.springboot.app.system_reservation_back.entity.exception;

import com.rodrigo.curso.springboot.app.system_reservation_back.entity.User;

public class UserException extends DomainException {

    public UserException(String message) {
        super(message);
    }

    public static UserException emailAlreadyExists(String email) {
        return new UserException("Email already exists: " + email);
    }

    public static UserException invalidPassword() {
        return new UserException(
                "Invalid password must have 8+ chars, 1 uppercase, 1 number, 1 symbol: "
        );
    }

    public static UserException invalidEmail(String email) {
        return new UserException("Invalid Email format: " + email);
    }

    public static UserException notFound(String id) {
        return new UserException("User not found: " + id);
    }
}
