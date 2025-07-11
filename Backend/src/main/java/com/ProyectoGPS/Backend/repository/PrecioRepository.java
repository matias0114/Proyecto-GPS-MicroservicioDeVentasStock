package com.ProyectoGPS.Backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ProyectoGPS.Backend.model.Precio;

@Repository
public interface PrecioRepository extends JpaRepository<Precio, Long> {
    // Métodos CRUD básicos heredados
}