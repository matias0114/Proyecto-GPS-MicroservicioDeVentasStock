package com.ProyectoGPS.Backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ProyectoGPS.Backend.dto.InventarioUploadRequest;
import com.ProyectoGPS.Backend.service.InventarioService;

@RestController
@RequestMapping("/api/inventarios")
public class InventarioController {


    @Autowired
    private InventarioService inventarioService;

    @PostMapping
    public ResponseEntity<String> guardarInventarios(@RequestBody List<InventarioUploadRequest> lista) {
        try {
            inventarioService.guardarInventarios(lista);
            return ResponseEntity.ok("Inventarios guardados correctamente.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al guardar inventarios: " + e.getMessage());
        }
    }
}
