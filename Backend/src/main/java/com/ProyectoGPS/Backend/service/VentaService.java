package com.ProyectoGPS.Backend.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ProyectoGPS.Backend.dto.DetalleVentaRequest;
import com.ProyectoGPS.Backend.dto.VentaUploadRequest;
import com.ProyectoGPS.Backend.model.DetalleVenta;
import com.ProyectoGPS.Backend.model.Producto;
import com.ProyectoGPS.Backend.model.Venta;
import com.ProyectoGPS.Backend.repository.ProductoRepository;
import com.ProyectoGPS.Backend.repository.VentaRepository;

import jakarta.transaction.Transactional;

@Service
public class VentaService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ProductoRepository productoRepository;

        @Transactional
    public void registrarVenta(VentaUploadRequest request) {
        Venta venta = new Venta();
        venta.setFecha(LocalDateTime.now());
        venta.setPacienteDni(request.getPacienteDni());

        List<DetalleVenta> detalles = new ArrayList<>();
        BigDecimal totalVenta = BigDecimal.ZERO;

        for (DetalleVentaRequest d : request.getDetalles()) {
            Producto producto = productoRepository.findByCodigo(d.getProductoCodigo())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + d.getProductoCodigo()));

            DetalleVenta detalle = new DetalleVenta();
            detalle.setProducto(producto);
            detalle.setCantidad(d.getCantidad());
            detalle.setPrecioUnitario(d.getPrecioUnitario());
            detalle.setVenta(venta);
            detalle.setProductoNombre(producto.getNombre());

            // Suma al total: precioUnitario * cantidad
            if (d.getPrecioUnitario() != null && d.getCantidad() != null) {
                totalVenta = totalVenta.add(d.getPrecioUnitario().multiply(BigDecimal.valueOf(d.getCantidad())));
            }

            detalles.add(detalle);
        }

        venta.setDetalles(detalles);
        venta.setTotal(totalVenta); // Guarda el total
        ventaRepository.save(venta);
    }
}
