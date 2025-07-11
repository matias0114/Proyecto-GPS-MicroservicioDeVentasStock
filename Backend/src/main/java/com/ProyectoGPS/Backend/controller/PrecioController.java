package com.ProyectoGPS.Backend.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ProyectoGPS.Backend.dto.PrecioUploadRequest;
import com.ProyectoGPS.Backend.model.Precio;
import com.ProyectoGPS.Backend.model.Producto;
import com.ProyectoGPS.Backend.repository.PrecioRepository;
import com.ProyectoGPS.Backend.repository.ProductoRepository;

@RestController
@RequestMapping("/api/precios")
public class PrecioController {

    @Autowired
    private PrecioRepository precioRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @PostMapping
    public ResponseEntity<?> crearPrecio(@RequestBody PrecioUploadRequest dto) {
        // findByCodigo ahora devuelve Optional<Producto>
        Optional<Producto> productoOpt = productoRepository.findByCodigo(dto.getProductoCodigo());
        if (productoOpt.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Producto con código " + dto.getProductoCodigo() + " no encontrado.");
        }
        Producto producto = productoOpt.get();

        Precio precio = new Precio();
        precio.setProducto(producto);
        precio.setPrecioUnitario(dto.getPrecioUnitario());
        precio.setFechaActualizacion(dto.getFechaActualizacion());

        Precio savedPrecio = precioRepository.save(precio);

        PrecioUploadRequest savedDto = new PrecioUploadRequest();
        savedDto.setId(savedPrecio.getId());
        savedDto.setProductoCodigo(producto.getCodigo());
        savedDto.setPrecioUnitario(savedPrecio.getPrecioUnitario());
        savedDto.setFechaActualizacion(savedPrecio.getFechaActualizacion());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDto);
    }

    @GetMapping
    public ResponseEntity<List<PrecioUploadRequest>> obtenerPrecios() {
        List<Precio> precios = precioRepository.findAll();
        List<PrecioUploadRequest> dtos = precios.stream().map(precio -> {
            PrecioUploadRequest dto = new PrecioUploadRequest();
            dto.setId(precio.getId());
            dto.setProductoCodigo(precio.getProducto().getCodigo());
            dto.setPrecioUnitario(precio.getPrecioUnitario());
            dto.setFechaActualizacion(precio.getFechaActualizacion());
            return dto;
        }).toList();
        return ResponseEntity.ok(dtos);
    }

        @GetMapping("/consulta")
    public ResponseEntity<?> consultarPrecio(@RequestParam String codigo) {
        var productoOpt = productoRepository.findByCodigo(codigo);
        if (productoOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Producto no encontrado");
        }
        // Busca el precio más reciente para el producto
        var precios = precioRepository.findAll().stream()
            .filter(p -> p.getProducto().getCodigo().equals(codigo))
            .sorted((a, b) -> b.getFechaActualizacion().compareTo(a.getFechaActualizacion()))
            .toList();
        if (precios.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Precio no encontrado para el producto");
        }
        return ResponseEntity.ok(precios.get(0));
    }
}
