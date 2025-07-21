package com.ProyectoGPS.Backend.Integracion;

import com.ProyectoGPS.Backend.client.InventoryServiceClient;
import com.ProyectoGPS.Backend.client.dto.InventoryResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.ProyectoGPS.Backend.controller.InventoryQueryController;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class InventoryQueryControllerIntegracionTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InventoryServiceClient inventoryServiceClient;

    private InventoryResponse sampleInventory;

    @BeforeEach
    void setUp() {
        sampleInventory = new InventoryResponse();
        sampleInventory.setCurrentStock(10);
    }

    @Test
    void testGetAllInventories_OK() throws Exception {
        Mockito.when(inventoryServiceClient.getAllInventories())
                .thenReturn(List.of(sampleInventory));

        mockMvc.perform(get("/api/inventory-query"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].currentStock").value(10));
    }

    @Test
    void testGetAllInventories_ServiceUnavailable() throws Exception {
        Mockito.when(inventoryServiceClient.getAllInventories())
                .thenThrow(new RuntimeException("Servicio fuera de línea"));

        mockMvc.perform(get("/api/inventory-query"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(header().exists("Error-Message"));
    }

    @Test
    void testGetInventoriesByWarehouse_OK() throws Exception {
        Mockito.when(inventoryServiceClient.getInventoriesByWarehouse(1L))
                .thenReturn(List.of(sampleInventory));

        mockMvc.perform(get("/api/inventory-query/warehouse/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].currentStock").value(10));
    }

    @Test
    void testGetInventoryById_OK() throws Exception {
        Mockito.when(inventoryServiceClient.getInventoryById(1L))
                .thenReturn(sampleInventory);

        mockMvc.perform(get("/api/inventory-query/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentStock").value(10));
    }

    @Test
    void testCheckStockAvailability_OK() throws Exception {
        Mockito.when(inventoryServiceClient.checkStockAvailability(1L, 5)).thenReturn(true);
        Mockito.when(inventoryServiceClient.getInventoryById(1L)).thenReturn(sampleInventory);

        mockMvc.perform(get("/api/inventory-query/1/stock-check")
                        .param("requiredQuantity", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.shortage").value(0));
    }

    @Test
    void testCheckInventoryServiceHealth_UP() throws Exception {
        Mockito.when(inventoryServiceClient.isServiceAvailable()).thenReturn(true);

        mockMvc.perform(get("/api/inventory-query/service-health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void testCheckInventoryServiceHealth_DOWN() throws Exception {
        Mockito.when(inventoryServiceClient.isServiceAvailable()).thenReturn(false);

        mockMvc.perform(get("/api/inventory-query/service-health"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status").value("DOWN"));
    }
}