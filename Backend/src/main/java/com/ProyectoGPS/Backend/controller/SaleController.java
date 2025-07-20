package com.ProyectoGPS.Backend.controller;

import com.ProyectoGPS.Backend.dto.SaleCreateDTO;
import com.ProyectoGPS.Backend.dto.SaleDTO;
import com.ProyectoGPS.Backend.service.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Controlador para la gestión de ventas y stock
 */
@RestController
@RequestMapping("/api/sales")
@CrossOrigin(origins = "*")
public class SaleController {

    @Autowired
    private SaleService saleService;

    /**
     * Crear una nueva venta
     */
    @PostMapping
    public ResponseEntity<SaleDTO> createSale(@RequestBody SaleCreateDTO saleCreateDTO) {
        try {
            SaleDTO createdSale = saleService.createSale(saleCreateDTO);
            return new ResponseEntity<>(createdSale, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .header("Error-Message", e.getMessage())
                    .build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("Error-Message", "Error interno del servidor")
                    .build();
        }
    }

    /**
     * Obtener todas las ventas
     */
    @GetMapping
    public ResponseEntity<List<SaleDTO>> getAllSales() {
        try {
            List<SaleDTO> sales = saleService.getAllSales();
            return ResponseEntity.ok(sales);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("Error-Message", "Error al obtener las ventas")
                    .build();
        }
    }

    /**
     * Obtener venta por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<SaleDTO> getSaleById(@PathVariable Long id) {
        try {
            SaleDTO sale = saleService.getSaleById(id);
            return ResponseEntity.ok(sale);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("Error-Message", "Error al obtener la venta")
                    .build();
        }
    }

    /**
     * Obtener ventas por RUT de paciente
     */
    @GetMapping("/patient/{rut}")
    public ResponseEntity<List<SaleDTO>> getSalesByPatientRut(@PathVariable String rut) {
        try {
            List<SaleDTO> sales = saleService.getSalesByPatientRut(rut);
            return ResponseEntity.ok(sales);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("Error-Message", "Error al obtener las ventas del paciente")
                    .build();
        }
    }

    /**
     * Obtener ventas del día actual
     */
    @GetMapping("/today")
    public ResponseEntity<List<SaleDTO>> getTodaysSales() {
        try {
            List<SaleDTO> sales = saleService.getTodaysSales();
            return ResponseEntity.ok(sales);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("Error-Message", "Error al obtener las ventas de hoy")
                    .build();
        }
    }

    /**
     * Cancelar una venta
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<SaleDTO> cancelSale(@PathVariable Long id) {
        try {
            SaleDTO cancelledSale = saleService.cancelSale(id);
            return ResponseEntity.ok(cancelledSale);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .header("Error-Message", e.getMessage())
                    .build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("Error-Message", "Error al cancelar la venta")
                    .build();
        }
    }

    /**
     * Obtener resumen de ventas por fecha
     */
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getSalesSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            // Si no se proporciona fecha, usar la fecha actual
            LocalDate targetDate = date != null ? date : LocalDate.now();
            
            Map<String, Object> summary = Map.of(
                "date", targetDate,
                "message", "Resumen de ventas - funcionalidad por implementar",
                "note", "Este endpoint será expandido con métricas específicas"
            );
            
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("Error-Message", "Error al obtener el resumen de ventas")
                    .build();
        }
    }

    /**
     * Endpoint de salud para verificar el estado del servicio
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> health = Map.of(
            "status", "UP",
            "service", "Sales and Stock Microservice",
            "timestamp", java.time.LocalDateTime.now().toString()
        );
        return ResponseEntity.ok(health);
    }

    /**
     * Manejo de errores global para este controlador
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException e) {
        Map<String, String> error = Map.of(
            "error", "Error en la operación",
            "message", e.getMessage(),
            "timestamp", java.time.LocalDateTime.now().toString()
        );
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception e) {
        Map<String, String> error = Map.of(
            "error", "Error interno del servidor",
            "message", "Ha ocurrido un error inesperado",
            "timestamp", java.time.LocalDateTime.now().toString()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
