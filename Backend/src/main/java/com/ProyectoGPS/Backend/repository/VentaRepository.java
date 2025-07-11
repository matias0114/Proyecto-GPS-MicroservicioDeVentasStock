package com.ProyectoGPS.Backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ProyectoGPS.Backend.model.Venta;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {}