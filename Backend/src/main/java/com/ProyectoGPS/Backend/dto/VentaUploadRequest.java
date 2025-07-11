package com.ProyectoGPS.Backend.dto;

import java.util.List;

public class VentaUploadRequest {
    private String pacienteDni;
    private List<DetalleVentaRequest> detalles;

    // Getters y setters
        public String getPacienteDni() {
        return pacienteDni;
    }

    public void setPacienteDni(String pacienteDni) {
        this.pacienteDni = pacienteDni;
    }

    public List<DetalleVentaRequest> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleVentaRequest> detalles) {
        this.detalles = detalles;
    }
    
}
