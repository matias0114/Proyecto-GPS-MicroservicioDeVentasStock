package com.ProyectoGPS.Backend.controller;

import com.ProyectoGPS.Backend.model.Producto;
import com.ProyectoGPS.Backend.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoRepository productoRepository;

    @PostMapping
    public ResponseEntity<Producto> crearProducto(@RequestBody Producto producto) {
        Producto guardado = productoRepository.save(producto);
        return ResponseEntity.ok(guardado);
    }
}