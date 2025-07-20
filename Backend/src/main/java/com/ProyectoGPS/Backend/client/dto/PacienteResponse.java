package com.ProyectoGPS.Backend.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;

/**
 * DTO para recibir respuestas de la API de Pacientes
 * No duplica el código, solo define la estructura de respuesta
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PacienteResponse {
    private Long id;
    private String nombre;
    private String apellido;
    private String rut;
    private LocalDate fechaNacimiento;
    private String direccion;
    private String telefono;
    private Boolean esBeneficiario;
    private String tipoBeneficio;

    // Constructores
    public PacienteResponse() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getRut() { return rut; }
    public void setRut(String rut) { this.rut = rut; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public Boolean getEsBeneficiario() { return esBeneficiario; }
    public void setEsBeneficiario(Boolean esBeneficiario) { this.esBeneficiario = esBeneficiario; }

    public String getTipoBeneficio() { return tipoBeneficio; }
    public void setTipoBeneficio(String tipoBeneficio) { this.tipoBeneficio = tipoBeneficio; }
}
