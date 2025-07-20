package com.ProyectoGPS.Backend.client;

import com.ProyectoGPS.Backend.client.dto.PacienteResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Cliente para comunicarse con el microservicio principal (Proyecto-GPS)
 * para obtener información de pacientes y beneficios
 */
@Component
public class PatientServiceClient {

    private final RestTemplate restTemplate;

    @Value("${app.services.patient.base-url:http://localhost:8080}")
    private String patientServiceBaseUrl;

    public PatientServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Obtener información de un paciente por RUT
     */
    public PacienteResponse getPatientByRut(String rut) {
        try {
            String url = patientServiceBaseUrl + "/api/pacientes/" + rut;
            ResponseEntity<PacienteResponse> response = restTemplate.getForEntity(url, PacienteResponse.class);
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException("Paciente con RUT " + rut + " no encontrado");
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("Error al consultar paciente: " + e.getMessage());
        } catch (ResourceAccessException e) {
            throw new RuntimeException("Servicio de pacientes no disponible: " + e.getMessage());
        }
    }

    /**
     * Obtener información de beneficios de un paciente por RUT
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getPatientBenefits(String rut) {
        try {
            String url = patientServiceBaseUrl + "/api/pacientes/" + rut + "/beneficios";
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            // Si no se encuentra el paciente, retornamos un mapa con beneficios por defecto
            return Map.of(
                "esBeneficiario", false,
                "tipoBeneficio", "NINGUNO",
                "descuentoPorcentaje", 0.0
            );
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("Error al consultar beneficios: " + e.getMessage());
        } catch (ResourceAccessException e) {
            // En caso de que el servicio no esté disponible, asumimos sin beneficios
            return Map.of(
                "esBeneficiario", false,
                "tipoBeneficio", "NINGUNO",
                "descuentoPorcentaje", 0.0
            );
        }
    }

    /**
     * Obtener tipos de beneficios disponibles
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getAvailableBenefitTypes() {
        try {
            String url = patientServiceBaseUrl + "/api/pacientes/beneficios/tipos";
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            return response.getBody();
        } catch (Exception e) {
            // Retornamos tipos de beneficios por defecto si el servicio no está disponible
            return Map.of(
                "ADULTO_MAYOR", Map.of("descripcion", "Adulto Mayor", "descuento", 15.0),
                "ESTUDIANTE", Map.of("descripcion", "Estudiante", "descuento", 10.0),
                "FUNCIONARIO_PUBLICO", Map.of("descripcion", "Funcionario Público", "descuento", 12.0),
                "DISCAPACIDAD", Map.of("descripcion", "Persona con Discapacidad", "descuento", 20.0)
            );
        }
    }

    /**
     * Verificar si el servicio de pacientes está disponible
     */
    public boolean isServiceAvailable() {
        try {
            String url = patientServiceBaseUrl + "/api/hola";
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            return false;
        }
    }
}
