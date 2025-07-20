package com.ProyectoGPS.Backend.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Date;

/**
 * DTO para recibir respuestas de la API de Inventarios
 * No duplica el código, solo define la estructura de respuesta
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class InventoryResponse {
    private Long id;
    private Long warehouseId;
    private Long batchId;
    private Integer quantity;
    private Integer currentStock;
    private String inventoryType;
    private Date lastUpdate;
    private WarehouseResponse warehouse;
    private BatchResponse batch;

    // Constructores
    public InventoryResponse() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getWarehouseId() { return warehouseId; }
    public void setWarehouseId(Long warehouseId) { this.warehouseId = warehouseId; }

    public Long getBatchId() { return batchId; }
    public void setBatchId(Long batchId) { this.batchId = batchId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Integer getCurrentStock() { return currentStock; }
    public void setCurrentStock(Integer currentStock) { this.currentStock = currentStock; }

    public String getInventoryType() { return inventoryType; }
    public void setInventoryType(String inventoryType) { this.inventoryType = inventoryType; }

    public Date getLastUpdate() { return lastUpdate; }
    public void setLastUpdate(Date lastUpdate) { this.lastUpdate = lastUpdate; }

    public WarehouseResponse getWarehouse() { return warehouse; }
    public void setWarehouse(WarehouseResponse warehouse) { this.warehouse = warehouse; }

    public BatchResponse getBatch() { return batch; }
    public void setBatch(BatchResponse batch) { this.batch = batch; }

    // DTOs anidados para las respuestas
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WarehouseResponse {
        private Long id;
        private String name;
        private String location;
        
        // Constructores, getters y setters
        public WarehouseResponse() {}
        
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BatchResponse {
        private Long id;
        private String batchNumber;
        private Date expirationDate;
        private Date manufacturingDate;
        private ProductResponse product;
        
        // Constructores, getters y setters
        public BatchResponse() {}
        
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getBatchNumber() { return batchNumber; }
        public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }
        
        public Date getExpirationDate() { return expirationDate; }
        public void setExpirationDate(Date expirationDate) { this.expirationDate = expirationDate; }
        
        public Date getManufacturingDate() { return manufacturingDate; }
        public void setManufacturingDate(Date manufacturingDate) { this.manufacturingDate = manufacturingDate; }
        
        public ProductResponse getProduct() { return product; }
        public void setProduct(ProductResponse product) { this.product = product; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ProductResponse {
        private Long id;
        private String name;
        private String description;
        private Double price;
        private String category;
        
        // Constructores, getters y setters
        public ProductResponse() {}
        
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public Double getPrice() { return price; }
        public void setPrice(Double price) { this.price = price; }
        
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
    }
    
    // Alias para mantener compatibilidad con el código existente
    public static class ProductDTO extends ProductResponse {}
    public static class WarehouseDTO extends WarehouseResponse {}
}
