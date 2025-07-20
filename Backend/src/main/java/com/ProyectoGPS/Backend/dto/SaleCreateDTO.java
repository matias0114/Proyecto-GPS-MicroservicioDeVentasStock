package com.ProyectoGPS.Backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para crear una nueva venta
 */
public class SaleCreateDTO {
    private String patientRut;
    private List<SaleItemCreateDTO> saleItems;

    // Constructores
    public SaleCreateDTO() {}

    public SaleCreateDTO(String patientRut, List<SaleItemCreateDTO> saleItems) {
        this.patientRut = patientRut;
        this.saleItems = saleItems;
    }

    // Getters y Setters
    public String getPatientRut() {
        return patientRut;
    }

    public void setPatientRut(String patientRut) {
        this.patientRut = patientRut;
    }

    public List<SaleItemCreateDTO> getSaleItems() {
        return saleItems;
    }

    public void setSaleItems(List<SaleItemCreateDTO> saleItems) {
        this.saleItems = saleItems;
    }

    /**
     * DTO anidado para los items de la venta
     */
    public static class SaleItemCreateDTO {
        private Long productId;
        private Long batchId;
        private Long warehouseId;
        private Integer quantity;

        // Constructores
        public SaleItemCreateDTO() {}

        public SaleItemCreateDTO(Long productId, Long batchId, Long warehouseId, Integer quantity) {
            this.productId = productId;
            this.batchId = batchId;
            this.warehouseId = warehouseId;
            this.quantity = quantity;
        }

        // Getters y Setters
        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public Long getBatchId() {
            return batchId;
        }

        public void setBatchId(Long batchId) {
            this.batchId = batchId;
        }

        public Long getWarehouseId() {
            return warehouseId;
        }

        public void setWarehouseId(Long warehouseId) {
            this.warehouseId = warehouseId;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }
}
