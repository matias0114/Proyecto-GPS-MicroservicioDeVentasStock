package com.ProyectoGPS.Backend.client;

import com.ProyectoGPS.Backend.client.dto.InventoryResponse;
import com.ProyectoGPS.Backend.dto.StockUpdateDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Cliente para comunicarse con el microservicio de gestión de inventarios
 */
@Component
public class InventoryServiceClient {

    private final RestTemplate restTemplate;

    @Value("${app.services.inventory.base-url:http://microservicio-gestion-de-inventarios:8081}")
    private String inventoryServiceBaseUrl;

    public InventoryServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Obtener todos los inventarios disponibles
     */
    public List<InventoryResponse> getAllInventories() {
        try {
            String url = inventoryServiceBaseUrl + "/api/inventory";
            ResponseEntity<List<InventoryResponse>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<InventoryResponse>>() {}
            );
            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("Error al consultar inventarios: " + e.getMessage());
        } catch (ResourceAccessException e) {
            throw new RuntimeException("Servicio de inventarios no disponible: " + e.getMessage());
        }
    }

    /**
     * Obtener inventarios por bodega
     */
    public List<InventoryResponse> getInventoriesByWarehouse(Long warehouseId) {
        try {
            String url = inventoryServiceBaseUrl + "/api/inventory/warehouse/" + warehouseId;
            ResponseEntity<List<InventoryResponse>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<InventoryResponse>>() {}
            );
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            return Collections.emptyList();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("Error al consultar inventarios por bodega: " + e.getMessage());
        } catch (ResourceAccessException e) {
            throw new RuntimeException("Servicio de inventarios no disponible: " + e.getMessage());
        }
    }

    /**
     * Obtener inventario específico por ID
     */
    public InventoryResponse getInventoryById(Long inventoryId) {
        try {
            String url = inventoryServiceBaseUrl + "/api/inventory/" + inventoryId;
            ResponseEntity<InventoryResponse> response = restTemplate.getForEntity(url, InventoryResponse.class);
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException("Inventario con ID " + inventoryId + " no encontrado");
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("Error al consultar inventario: " + e.getMessage());
        } catch (ResourceAccessException e) {
            throw new RuntimeException("Servicio de inventarios no disponible: " + e.getMessage());
        }
    }

    /**
     * Buscar inventarios por lote y bodega específicos
     */
    public InventoryResponse getInventoryByBatchAndWarehouse(Long batchId, Long warehouseId) {
        try {
            String url = inventoryServiceBaseUrl + "/api/inventory/batch/" + batchId + "/warehouse/" + warehouseId;
            ResponseEntity<InventoryResponse> response = restTemplate.getForEntity(url, InventoryResponse.class);
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException("No se encontró inventario para el lote " + batchId + " en la bodega " + warehouseId);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("Error al consultar inventario específico: " + e.getMessage());
        } catch (ResourceAccessException e) {
            throw new RuntimeException("Servicio de inventarios no disponible: " + e.getMessage());
        }
    }

    /**
     * Actualizar stock en el inventario (reducir por venta)
     */
    public boolean updateStock(Long inventoryId, Integer quantityToReduce) {
        try {
            String url = inventoryServiceBaseUrl + "/api/inventory/" + inventoryId + "/reduce-stock";
            
            Map<String, Object> updateRequest = Map.of(
                "quantity", quantityToReduce,
                "reason", "SALE"
            );
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(updateRequest);
            ResponseEntity<Void> response = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                request,
                Void.class
            );
            
            return response.getStatusCode() == HttpStatus.OK;
        } catch (HttpClientErrorException.BadRequest e) {
            throw new RuntimeException("Stock insuficiente para la operación");
        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException("Inventario no encontrado");
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("Error al actualizar stock: " + e.getMessage());
        } catch (ResourceAccessException e) {
            throw new RuntimeException("Servicio de inventarios no disponible: " + e.getMessage());
        }
    }

    /**
     * Verificar disponibilidad de stock
     */
    public boolean checkStockAvailability(Long inventoryId, Integer requiredQuantity) {
        try {
            InventoryResponse inventory = getInventoryById(inventoryId);
            return inventory != null && inventory.getCurrentStock() >= requiredQuantity;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Incrementar stock en el inventario (para cancelaciones de venta)
     */
    public boolean incrementStock(Long inventoryId, Integer quantityToAdd) {
        try {
            String url = inventoryServiceBaseUrl + "/api/inventory/" + inventoryId + "/add-stock";
            
            Map<String, Object> updateRequest = Map.of(
                "quantity", quantityToAdd,
                "reason", "CANCELLATION"
            );
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(updateRequest);
            ResponseEntity<Void> response = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                request,
                Void.class
            );
            
            return response.getStatusCode() == HttpStatus.OK;
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("Error al incrementar stock: " + e.getMessage());
        } catch (ResourceAccessException e) {
            throw new RuntimeException("Servicio de inventarios no disponible: " + e.getMessage());
        }
    }

    /**
     * Obtener productos disponibles
     */
    public List<InventoryResponse.ProductResponse> getAvailableProducts() {
        try {
            String url = inventoryServiceBaseUrl + "/api/products";
            ResponseEntity<List<InventoryResponse.ProductResponse>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<InventoryResponse.ProductResponse>>() {}
            );
            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("Error al consultar productos: " + e.getMessage());
        } catch (ResourceAccessException e) {
            throw new RuntimeException("Servicio de inventarios no disponible: " + e.getMessage());
        }
    }

    /**
     * Obtener bodegas disponibles
     */
    public List<InventoryResponse.WarehouseResponse> getAvailableWarehouses() {
        try {
            String url = inventoryServiceBaseUrl + "/api/warehouse";
            ResponseEntity<List<InventoryResponse.WarehouseResponse>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<InventoryResponse.WarehouseResponse>>() {}
            );
            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("Error al consultar bodegas: " + e.getMessage());
        } catch (ResourceAccessException e) {
            throw new RuntimeException("Servicio de inventarios no disponible: " + e.getMessage());
        }
    }

    /**
     * Verificar si el servicio de inventarios está disponible
     */
    public boolean isServiceAvailable() {
        try {
            String url = inventoryServiceBaseUrl + "/api/hola";
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            return false;
        }
    }
}
