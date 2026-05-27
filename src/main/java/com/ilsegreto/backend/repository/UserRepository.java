package com.ilsegreto.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ilsegreto.backend.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    // Método para checar se o e-mail do Google já está cadastrado
    Optional<User> findByEmail(String email);
}