package com.ProyectoGPS.Backend;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.ProyectoGPS.Backend.client.InventoryServiceClient;
import com.ProyectoGPS.Backend.client.dto.InventoryResponse;
import com.ProyectoGPS.Backend.controller.InventoryQueryController;

class InventoryQueryControllerTest {

    @Mock
    private InventoryServiceClient inventoryServiceClient;

    @InjectMocks
    private InventoryQueryController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllInventories_success() {
        List<InventoryResponse> mockList = List.of(new InventoryResponse());
        when(inventoryServiceClient.getAllInventories()).thenReturn(mockList);

        ResponseEntity<List<InventoryResponse>> response = controller.getAllInventories();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockList, response.getBody());
    }

    @Test
    void testGetAllInventories_runtimeException() {
        when(inventoryServiceClient.getAllInventories()).thenThrow(new RuntimeException("MS no disponible"));

        ResponseEntity<List<InventoryResponse>> response = controller.getAllInventories();

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals("MS no disponible", response.getHeaders().getFirst("Error-Message"));
    }

    @Test
    void testGetInventoriesByWarehouse_success() {
        List<InventoryResponse> mockList = List.of(new InventoryResponse());
        when(inventoryServiceClient.getInventoriesByWarehouse(1L)).thenReturn(mockList);

        ResponseEntity<List<InventoryResponse>> response = controller.getInventoriesByWarehouse(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockList, response.getBody());
    }

    @Test
    void testGetInventoriesByWarehouse_runtimeException() {
        when(inventoryServiceClient.getInventoriesByWarehouse(1L)).thenThrow(new RuntimeException("Bodega caída"));

        ResponseEntity<List<InventoryResponse>> response = controller.getInventoriesByWarehouse(1L);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals("Bodega caída", response.getHeaders().getFirst("Error-Message"));
    }

    @Test
    void testGetInventoryById_success() {
        InventoryResponse inv = new InventoryResponse();
        when(inventoryServiceClient.getInventoryById(99L)).thenReturn(inv);

        ResponseEntity<InventoryResponse> response = controller.getInventoryById(99L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(inv, response.getBody());
    }

    @Test
    void testGetInventoryById_runtimeException() {
        when(inventoryServiceClient.getInventoryById(99L)).thenThrow(new RuntimeException("No encontrado"));

        ResponseEntity<InventoryResponse> response = controller.getInventoryById(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testCheckStockAvailability_available() {
        InventoryResponse inv = new InventoryResponse();
        inv.setCurrentStock(10);
        when(inventoryServiceClient.checkStockAvailability(1L, 5)).thenReturn(true);
        when(inventoryServiceClient.getInventoryById(1L)).thenReturn(inv);

        ResponseEntity<Map<String, Object>> response = controller.checkStockAvailability(1L, 5);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue((Boolean) response.getBody().get("available"));
        assertEquals(0, response.getBody().get("shortage"));
    }

    @Test
    void testCheckStockAvailability_notAvailable() {
        InventoryResponse inv = new InventoryResponse();
        inv.setCurrentStock(3);
        when(inventoryServiceClient.checkStockAvailability(1L, 5)).thenReturn(false);
        when(inventoryServiceClient.getInventoryById(1L)).thenReturn(inv);

        ResponseEntity<Map<String, Object>> response = controller.checkStockAvailability(1L, 5);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse((Boolean) response.getBody().get("available"));
        assertEquals(2, response.getBody().get("shortage"));
    }

    @Test
    void testCheckStockAvailability_runtimeException() {
        when(inventoryServiceClient.checkStockAvailability(1L, 5)).thenThrow(new RuntimeException());

        ResponseEntity<Map<String, Object>> response = controller.checkStockAvailability(1L, 5);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetAvailableProducts_success() {
        List<InventoryResponse.ProductResponse> mockList = List.of(new InventoryResponse.ProductResponse());
        when(inventoryServiceClient.getAvailableProducts()).thenReturn(mockList);

        ResponseEntity<List<InventoryResponse.ProductResponse>> response = controller.getAvailableProducts();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockList, response.getBody());
    }

    @Test
    void testGetAvailableWarehouses_success() {
        List<InventoryResponse.WarehouseResponse> mockList = List.of(new InventoryResponse.WarehouseResponse());
        when(inventoryServiceClient.getAvailableWarehouses()).thenReturn(mockList);

        ResponseEntity<List<InventoryResponse.WarehouseResponse>> response = controller.getAvailableWarehouses();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockList, response.getBody());
    }

    @Test
    void testCheckInventoryServiceHealth_up() {
        when(inventoryServiceClient.isServiceAvailable()).thenReturn(true);

        ResponseEntity<Map<String, Object>> response = controller.checkInventoryServiceHealth();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("UP", response.getBody().get("status"));
    }

    @Test
    void testCheckInventoryServiceHealth_down() {
        when(inventoryServiceClient.isServiceAvailable()).thenReturn(false);

        ResponseEntity<Map<String, Object>> response = controller.checkInventoryServiceHealth();

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals("DOWN", response.getBody().get("status"));
    }

    @Test
    void testHandleRuntimeException() {
        RuntimeException ex = new RuntimeException("error inventario");
        ResponseEntity<Map<String, String>> response = controller.handleRuntimeException(ex);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals("error inventario", response.getBody().get("message"));
        assertEquals("Error en consulta de inventario", response.getBody().get("error"));
        assertNotNull(response.getBody().get("timestamp"));
    }
}