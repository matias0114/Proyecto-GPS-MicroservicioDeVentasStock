package com.ProyectoGPS.Backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ProyectoGPS.Backend.model.Paciente;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    Optional<Paciente> findByDni(String dni);
}

