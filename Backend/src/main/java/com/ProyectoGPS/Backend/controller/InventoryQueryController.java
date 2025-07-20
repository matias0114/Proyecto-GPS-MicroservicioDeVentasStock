package com.ProyectoGPS.Backend.controller;

import com.ProyectoGPS.Backend.client.InventoryServiceClient;
import com.ProyectoGPS.Backend.client.dto.InventoryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador para consultas de inventario desde el microservicio de ventas
 */
@RestController
@RequestMapping("/api/inventory-query")
@CrossOrigin(origins = "*")
public class InventoryQueryController {

    @Autowired
    private InventoryServiceClient inventoryServiceClient;

    /**
     * Obtener todos los inventarios disponibles
     */
    @GetMapping
    public ResponseEntity<List<InventoryResponse>> getAllInventories() {
        try {
            List<InventoryResponse> inventories = inventoryServiceClient.getAllInventories();
            return ResponseEntity.ok(inventories);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .header("Error-Message", e.getMessage())
                    .build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("Error-Message", "Error al obtener inventarios")
                    .build();
        }
    }

    /**
     * Obtener inventarios por bodega
     */
    @GetMapping("/warehouse/{warehouseId}")
    public ResponseEntity<List<InventoryResponse>> getInventoriesByWarehouse(@PathVariable Long warehouseId) {
        try {
            List<InventoryResponse> inventories = inventoryServiceClient.getInventoriesByWarehouse(warehouseId);
            return ResponseEntity.ok(inventories);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .header("Error-Message", e.getMessage())
                    .build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("Error-Message", "Error al obtener inventarios por bodega")
                    .build();
        }
    }

    /**
     * Obtener inventario específico
     */
    @GetMapping("/{inventoryId}")
    public ResponseEntity<InventoryResponse> getInventoryById(@PathVariable Long inventoryId) {
        try {
            InventoryResponse inventory = inventoryServiceClient.getInventoryById(inventoryId);
            return ResponseEntity.ok(inventory);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("Error-Message", "Error al obtener inventario")
                    .build();
        }
    }

    /**
     * Verificar disponibilidad de stock
     */
    @GetMapping("/{inventoryId}/stock-check")
    public ResponseEntity<Map<String, Object>> checkStockAvailability(
            @PathVariable Long inventoryId,
            @RequestParam Integer requiredQuantity) {
        try {
            boolean available = inventoryServiceClient.checkStockAvailability(inventoryId, requiredQuantity);
            InventoryResponse inventory = inventoryServiceClient.getInventoryById(inventoryId);
            
            Map<String, Object> response = Map.of(
                "inventoryId", inventoryId,
                "requiredQuantity", requiredQuantity,
                "currentStock", inventory.getCurrentStock(),
                "available", available,
                "shortage", available ? 0 : requiredQuantity - inventory.getCurrentStock()
            );
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("Error-Message", "Error al verificar disponibilidad")
                    .build();
        }
    }

    /**
     * Obtener productos disponibles
     */
    @GetMapping("/products")
    public ResponseEntity<List<InventoryResponse.ProductResponse>> getAvailableProducts() {
        try {
            List<InventoryResponse.ProductResponse> products = inventoryServiceClient.getAvailableProducts();
            return ResponseEntity.ok(products);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .header("Error-Message", e.getMessage())
                    .build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("Error-Message", "Error al obtener productos")
                    .build();
        }
    }

    /**
     * Obtener bodegas disponibles
     */
    @GetMapping("/warehouses")
    public ResponseEntity<List<InventoryResponse.WarehouseResponse>> getAvailableWarehouses() {
        try {
            List<InventoryResponse.WarehouseResponse> warehouses = inventoryServiceClient.getAvailableWarehouses();
            return ResponseEntity.ok(warehouses);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .header("Error-Message", e.getMessage())
                    .build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("Error-Message", "Error al obtener bodegas")
                    .build();
        }
    }

    /**
     * Verificar estado del servicio de inventarios
     */
    @GetMapping("/service-health")
    public ResponseEntity<Map<String, Object>> checkInventoryServiceHealth() {
        boolean isAvailable = inventoryServiceClient.isServiceAvailable();
        
        Map<String, Object> health = Map.of(
            "inventoryServiceAvailable", isAvailable,
            "status", isAvailable ? "UP" : "DOWN",
            "timestamp", java.time.LocalDateTime.now().toString()
        );
        
        HttpStatus status = isAvailable ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE;
        return ResponseEntity.status(status).body(health);
    }

    /**
     * Manejo de errores específico para este controlador
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException e) {
        Map<String, String> error = Map.of(
            "error", "Error en consulta de inventario",
            "message", e.getMessage(),
            "timestamp", java.time.LocalDateTime.now().toString()
        );
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
    }
}
