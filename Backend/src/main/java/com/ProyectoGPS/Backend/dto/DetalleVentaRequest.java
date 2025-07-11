package com.ProyectoGPS.Backend.dto;

import java.math.BigDecimal;

public class DetalleVentaRequest {
    private String productoCodigo;
    private Integer cantidad;
    private BigDecimal precioUnitario;

    // Getters y setters
    public String getProductoCodigo() {
        return productoCodigo;
    }
    public void setProductoCodigo(String productoCodigo) {
        this.productoCodigo = productoCodigo;
    }
    public Integer getCantidad() {
        return cantidad;
    }
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }
    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
    
}
