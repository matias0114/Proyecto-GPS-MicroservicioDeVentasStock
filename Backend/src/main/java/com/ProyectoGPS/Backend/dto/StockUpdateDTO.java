package com.ProyectoGPS.Backend.dto;

import java.math.BigDecimal;

/**
 * DTO para actualizar el stock en el microservicio de inventarios
 */
public class StockUpdateDTO {
    private Long inventoryId;
    private Integer quantityToReduce;
    private String reason;

    // Constructores
    public StockUpdateDTO() {}

    public StockUpdateDTO(Long inventoryId, Integer quantityToReduce, String reason) {
        this.inventoryId = inventoryId;
        this.quantityToReduce = quantityToReduce;
        this.reason = reason;
    }

    // Getters y Setters
    public Long getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(Long inventoryId) {
        this.inventoryId = inventoryId;
    }

    public Integer getQuantityToReduce() {
        return quantityToReduce;
    }

    public void setQuantityToReduce(Integer quantityToReduce) {
        this.quantityToReduce = quantityToReduce;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
