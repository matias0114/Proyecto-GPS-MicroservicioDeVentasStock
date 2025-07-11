package com.ProyectoGPS.Backend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ProyectoGPS.Backend.dto.InventarioUploadRequest;
import com.ProyectoGPS.Backend.model.Inventario;
import com.ProyectoGPS.Backend.model.Producto;
import com.ProyectoGPS.Backend.repository.InventarioRepository;
import com.ProyectoGPS.Backend.repository.ProductoRepository;

import jakarta.transaction.Transactional;

@Service
public class InventarioService {

    @Autowired
    private InventarioRepository inventarioRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Transactional
    public void guardarInventarios(List<InventarioUploadRequest> lista) {
        for (InventarioUploadRequest req : lista) {
            // BUSCAR el producto por su código, no por ID numérico
            Producto producto = productoRepository
                .findByCodigo(req.getProductoCodigo())
                .orElseThrow(() -> new RuntimeException(
                    "Producto no encontrado: " + req.getProductoCodigo()
                ));


            Inventario entidad = new Inventario();
            entidad.setProducto(producto);
            entidad.setCantidadInicial(req.getCantidadInicial());
            entidad.setFechaRegistro(LocalDateTime.now());
            inventarioRepository.save(entidad);
        }
    }

}
