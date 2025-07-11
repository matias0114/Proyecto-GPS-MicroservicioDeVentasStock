package com.ProyectoGPS.Backend.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ProyectoGPS.Backend.model.Inventario;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Long> {
}