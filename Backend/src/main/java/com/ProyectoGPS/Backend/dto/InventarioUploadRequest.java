package com.ProyectoGPS.Backend.dto;

public class InventarioUploadRequest {
    private String productoCodigo;
    private Integer cantidadInicial;

    // Getters y setters
    public String getProductoCodigo() {
        return productoCodigo;
    }
    public void setProductoCodigo(String productoCodigo) {
        this.productoCodigo = productoCodigo;
    }
    public Integer getCantidadInicial() {
        return cantidadInicial;
    }
    public void setCantidadInicial(Integer cantidadInicial) {
        this.cantidadInicial = cantidadInicial;
    }
}