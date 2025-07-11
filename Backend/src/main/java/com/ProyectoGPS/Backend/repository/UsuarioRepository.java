package com.ProyectoGPS.Backend.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.ProyectoGPS.Backend.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // No hace falta código extra para contar, JpaRepository ya lo provee
}

